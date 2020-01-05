package com.javashitang.rabbitmq.chapter_6_dlx.notResetRoutingKey;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
@Slf4j
public class NormalConsumer {

    public static final String DLX_EXCHANGE_NAME = "accept_dlx_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlxNormalQueue";
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);

        channel.queueDeclare(queueName, false, true, false, argsMap);

        channel.queueBind(queueName, DlxProducer.EXCHANGE_NAME, "#");

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                if (envelope.getRoutingKey().equals("error")) {
                    log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } else {
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }
}
