package com.javashitang.rabbitmq.chapter_8_transaction;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 事务效率比较低，生产环境中一般用发布方确认（publisher confirm）来代替事务
 */
@Slf4j
public class TransactionProducer {

    public static final String EXCHANGE_NAME = "transaction_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String routingKey = "error";
        // 将当前信道设置成事务模式
        channel.txSelect();
        for (int i = 0; i < 10; i++) {
            String message = "hello rabbitmq " + i;
            try {
                channel.basicPublish(EXCHANGE_NAME, routingKey, false, null, message.getBytes());
                log.info("send message, routingKey: {}, message: {}", routingKey, message);
                // 提交事务
                channel.txCommit();
            } catch (Exception e) {
                e.printStackTrace();
                // 回滚事务
                channel.txRollback();
            }
        }

        channel.close();
        connection.close();
    }
}
