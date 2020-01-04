package com.javashitang.rabbitmq.chapter_5_backupExchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
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
public class MainConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(BackupExProducer.BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("alternate-exchange", BackupExProducer.BAK_EXCHANGE_NAME);

        channel.exchangeDeclare(BackupExProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, argsMap);;

        String queueName = "errorQueue";
        channel.queueDeclare(queueName, false, false, false, null);

        String bindingKey = "error";
        channel.queueBind(queueName, BackupExProducer.EXCHANGE_NAME, bindingKey);

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
