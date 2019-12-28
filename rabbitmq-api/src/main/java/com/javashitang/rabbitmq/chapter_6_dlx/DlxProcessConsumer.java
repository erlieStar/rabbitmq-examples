package com.javashitang.rabbitmq.chapter_6_dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
public class DlxProcessConsumer {

    public final static String DLX_EXCHANGE_NAME = "dlx_accept";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlx_accept";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, DLX_EXCHANGE_NAME, "#");

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(envelope.getRoutingKey() + " " + message);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}
