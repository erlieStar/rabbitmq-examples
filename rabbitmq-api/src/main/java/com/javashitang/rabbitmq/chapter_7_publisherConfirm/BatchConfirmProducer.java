package com.javashitang.rabbitmq.chapter_7_publisherConfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class BatchConfirmProducer {

    public static final String EXCHANGE_NAME = "batch_confirm_exchange";

    public static void main(String[] args)
            throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 启用发布者确认模式
        channel.confirmSelect();

        String routingKey = "error";
        for (int i = 0; i < 10; i++) {
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            if ((i & 1) == 1) {
                // 发送的2条消息都确认了才会继续发送
                channel.waitForConfirmsOrDie();
                log.info("wait confirm");
            }
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }
        channel.close();
        connection.close();
    }
}
