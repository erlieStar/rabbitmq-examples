package com.javashitang.rabbitmq.chapter_2_exchange.same;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class TopicExchangeProducer {

    public static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("myhost");
        connectionFactory.setPort(5672);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        for (int i = 0; i < 3; i++) {
            String routingKey = "test";
            String message = "hello rabbitmq routingKey is " + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }

        channel.close();
        connection.close();
    }
}
