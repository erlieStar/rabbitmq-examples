package com.javashitang.rabbitmq.chapter_6_dlx.notResetRoutingKey;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 当消息被拒绝，并且requeue=false时，最好将不能处理的消息投入死信队列供以后处理
 * 消息被路由到死信交换器的时候，可以重新设置路由键（如果不设置默认是消息原来的路由键）
 * 所以死信队列这块分了2个包，notResetRoutingKey 不重新设置路由键，resetRoutingKey 重新设置路由键
 */
@Slf4j
public class DlxProducer {

    public static final String EXCHANGE_NAME = "dlx_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("myhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String[] logLevel = {"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String routingKey = logLevel[i % 3];
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey ,message);
        }
        channel.close();
        connection.close();
    }
}
