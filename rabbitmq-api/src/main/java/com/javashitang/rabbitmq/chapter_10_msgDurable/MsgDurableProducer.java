package com.javashitang.rabbitmq.chapter_10_msgDurable;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 做消息持久化的时候，队列也得做持久化，不然RabbitMQ重启后，队列消失，消息也会消失
 */
@Slf4j
public class MsgDurableProducer {

    public static final String EXCHANGE_NAME = "msg_durable_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String routingKey = "error";
        for (int i = 0; i < 10; i++) {
            String message = "hello rabbit " + i;
            // 主要就是将deliveryMode设置为2
            channel.basicPublish(EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }
        channel.close();
        connection.close();
    }
}
