package com.javashitang.rabbitmq.chapter_8_transaction;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TransactionProducer {

    public final static String EXCHANGE_NAME = "transaction_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String[] logLevel = {"error", "info", "warning"};
        // 将当前信道设置成事务模式
        channel.txSelect();
        for (int i = 0; i < 10; i++) {
            String routingKey = logLevel[i % 3];
            String message = "hello rabbitmq " + i;
            try {
                channel.basicPublish(EXCHANGE_NAME, routingKey, false, null, message.getBytes());
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
