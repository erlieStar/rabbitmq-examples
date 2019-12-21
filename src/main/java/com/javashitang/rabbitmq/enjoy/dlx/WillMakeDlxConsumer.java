package com.javashitang.rabbitmq.enjoy.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
public class WillMakeDlxConsumer {


    public static void main(String[] arv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.erlie.cc");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DlxProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlx_make";
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",
            DlxProcessConsumer.DLX_EXCHANGE_NAME);

        channel.queueDeclare(queueName, false, true, false, args);

        channel.queueBind(queueName, DlxProducer.EXCHANGE_NAME, "#");

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                if (envelope.getRoutingKey().equals("error")) {
                    System.out.println(envelope.getRoutingKey() + " " + message);
                    channel.basicReject(envelope.getDeliveryTag(),
                        false);
                } else {
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }
}
