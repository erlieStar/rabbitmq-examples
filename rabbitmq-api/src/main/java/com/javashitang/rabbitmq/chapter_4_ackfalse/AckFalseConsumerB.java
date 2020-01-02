package com.javashitang.rabbitmq.chapter_4_ackfalse;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
public class AckFalseConsumerB {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(AckFalseProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "focusError";
        channel.queueDeclare(queueName, false, false, false, null);

        String bindingKey = "error";
        channel.queueBind(queueName, AckFalseProducer.EXCHANGE_NAME, bindingKey);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("receive message, routingKey is " + envelope.getRoutingKey() + " message is " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }
}
