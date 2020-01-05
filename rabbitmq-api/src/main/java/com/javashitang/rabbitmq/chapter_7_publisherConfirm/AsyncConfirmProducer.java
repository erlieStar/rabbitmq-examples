package com.javashitang.rabbitmq.chapter_7_publisherConfirm;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class AsyncConfirmProducer {

    public static final String EXCHANGE_NAME = "async_confirm_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 启用发布者确认模式
        channel.confirmSelect();

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("send message error, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                        replyCode, replyText, exchange, routingKey);
            }
        });

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                log.info("handleAck, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                log.info("handleNack, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
            }
        });

        String routingKey = "error";
        for (int i = 0; i < 10; i++) {
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, true, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }

    }
}
