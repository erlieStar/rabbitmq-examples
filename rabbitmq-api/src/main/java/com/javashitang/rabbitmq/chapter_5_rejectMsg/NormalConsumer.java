package com.javashitang.rabbitmq.chapter_5_rejectMsg;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
@Slf4j
public class NormalConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(RejectMsgProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "errorQueue";
        channel.queueDeclare(queueName, false, false, false, null);

        String bindingKey = "error";
        channel.queueBind(queueName, RejectMsgProducer.EXCHANGE_NAME, bindingKey);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), false);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey() ,message);
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }
}
