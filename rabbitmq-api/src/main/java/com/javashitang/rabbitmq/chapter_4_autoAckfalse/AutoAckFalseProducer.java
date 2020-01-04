package com.javashitang.rabbitmq.chapter_4_autoAckfalse;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一般情况下，如果队列中的消息发送到消费者后，消费者不对消息进行确认。
 * 那么消息会一直留在队列中，直到确认才会删除。
 * 消费者与rabbitmq的连接中断，rabbitmq才会考虑将消息重新投递给另一个消费者
 *
 * 我们可以通过如下步骤来演示
 * 1. 开启 AutoAckFalseConsumerA, AutoAckFalseConsumerB, RejectMsgProducer
 * 2. 此时AutoAckFalseConsumerA 和 AutoAckFalseConsumerB 都会收到消息，但是AutoAckFalseConsumerA没有对消息进行确认
 * 3. 关闭AutoAckFalseConsumerA，此时可以看到AutoAckFalseConsumerB又收到AutoAckFalseConsumerA收到的消息
 */
@Slf4j
public class AutoAckFalseProducer {

    public static final String EXCHANGE_NAME = "ackFalse_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String routingKey = "error";
        for (int i = 0; i < 3; i++) {
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey ,message);
        }
        channel.close();
        connection.close();
    }
}
