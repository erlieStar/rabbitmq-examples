package com.javashitang.rabbitmq.chapter_6_dlx.resetRoutingKey;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DlxProducer {

    public final static String EXCHANGE_NAME = "dlx_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String[] logLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String routingKey = logLevel[i % 3];
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey ,message);
        }
        channel.close();
        connection.close();
    }
}
