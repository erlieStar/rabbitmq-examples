package com.javashitang.rabbitmq.chapter_7_publisherConfirm;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Channel对象提供的ConfirmListener()回调方法只包含deliveryTag（当前Chanel发出的消息序号），
 * 我们需要自己为每一个Channel维护一个unconfirm的消息序号集合，
 * 每publish一条数据，集合中元素加1，每回调一次handleAck方法，
 * unconfirm集合删掉相应的一条（multiple=false）或多条（multiple=true）记录。
 * 从程序运行效率上看，这个unconfirm集合最好采用有序集合SortedSet存储结构
 *
 * 参考自《RabbitMQ实战指南》
 */
@Slf4j
public class AsyncConfirmProducer {

    public static final String EXCHANGE_NAME = "async_confirm_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

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
