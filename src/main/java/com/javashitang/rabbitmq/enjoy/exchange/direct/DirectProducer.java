package com.javashitang.rabbitmq.enjoy.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:48
 */
public class DirectProducer {

    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.erlie.cc");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String[] level = {"error", "info", "warning"};
        for (int i = 0; i < 3; i++) {
            String routingKey = level[i % 3];
            String message = "Hello Rabbitmq" + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        }
        channel.close();
        connection.close();
    }
}
