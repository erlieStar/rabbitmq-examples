package com.javashitang.rabbitmq.chapter_12_failureNotice;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 失败通知
 * 当消息不可路由时，我们如何获取到不能正确路由的消息呢？
 * 1. channel.basicPublish()方法的mandatory参数设置为true
 * 2. channel.addReturnListener()添加监听器
 */
@Slf4j
public class FailureNoticeProducer {

    public static final String EXCHANGE_NAME  = "failureNotice_exchange";

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.javashitang.com");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.exchangeDeclare(FailureNoticeProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("send message error, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                        replyCode, replyText, exchange, routingKey);
            }
        });

        String[] logLevel = {"error","info"};
        for (int i = 0; i < 5; i++) {
            String routingKey = logLevel[i % 2];
            String message = "hello rabbitmq " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey,  true, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }
    }
}
