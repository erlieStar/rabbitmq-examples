package com.javashitang.rabbitmq.chapter_2_exchange.topic;

import com.javashitang.rabbitmq.chapter_2_exchange.direct.DirectExchangeProducer;
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

@Slf4j
public class FocusCrmConsumer {

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("myhost");
        connectionFactory.setPort(5672);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        // 声明一个交换机
        channel.exchangeDeclare(TopicExchangeProducer.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = "crmQueue";
        String bindingKey = "#.crm.#";

        channel.queueDeclare(queueName, false, false, false ,null);
        channel.queueBind(queueName, DirectExchangeProducer.EXCHANGE_NAME, bindingKey);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        channel.basicConsume(queueName , true, consumer);
    }
}
