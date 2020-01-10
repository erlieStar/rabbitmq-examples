package com.javashitang.rabbitmq.chapter_12_qos;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 使用qos的步骤
 * 1. autoAck设置为false
 * 2. 调用basicConsume方法前先调用basicQos方法
 */
@Slf4j
public class QosConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(QosProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "qosQueue";
        channel.queueDeclare(queueName, false, false, false, null);

        channel.queueBind(queueName, QosProducer.EXCHANGE_NAME, "error");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                channel.basicAck(envelope.getDeliveryTag(), true);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey() ,message);
            }
        };

        // prefetchSize为批量取的消息的总大小，0为不限制，rabbitmq目前实现
        // prefetchCount为消费完3条（3条消息被ack）才开始开始推送，
        // global为true表示对channel进行限制，否则对每个消费者进行限制
        // 一个信道允许有多个消费者
        channel.basicQos(0, 3, false);
        channel.basicConsume(queueName, false, consumer);
    }
}
