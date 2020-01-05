package com.javashitang.rabbitmq.chapter_6_dlx.resetRoutingKey;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
@Slf4j
public class ProcessLogDlxConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(NormalConsumer.DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlxLogQueue";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, NormalConsumer.DLX_EXCHANGE_NAME, NormalConsumer.DLX_ROUTE_KEY);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}
