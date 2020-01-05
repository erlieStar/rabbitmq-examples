package com.javashitang.rabbitmq.chapter_6_dlx.resetRoutingKey;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * NormalConsumer是普通消费者，投入死信交换器的时候，会重新设置routingKey=log
 * 而此时共有2个队列绑定在死信交换器上 ProcessLogDlxConsumer （bindingdKey=log）
 * ProcessOtherDlxConsumer(bindingdKey=other) 所以这个demo中 ProcessLogDlxConsumer 会收到消息
 * 而 ProcessOtherDlxConsumer 收不到消息
 */
@Slf4j
public class NormalConsumer {

    public static final String DLX_EXCHANGE_NAME = "accept_dlx_exchange";
    public static final String DLX_ROUTE_KEY = "log";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "dlxNormalQueue";
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        argsMap.put("x-dead-letter-routing-key", DLX_ROUTE_KEY);

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
