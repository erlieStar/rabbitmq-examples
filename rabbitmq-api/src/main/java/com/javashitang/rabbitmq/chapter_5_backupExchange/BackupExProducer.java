package com.javashitang.rabbitmq.chapter_5_backupExchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:32
 */
public class BackupExProducer {

    public final static String EXCHANGE_NAME = "main_exchange";
    public final static String BAK_EXCHANGE_NAME = "backup_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("alternate-exchange", BAK_EXCHANGE_NAME);

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, argsMap);

        // 备用交换器
        // Fanout Exchange
        // 发送到交换机的消息都会被转发到与该交换机绑定的所有队列上
        channel.exchangeDeclare(BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, false, false, null);

        String[] logLevel ={"error","info","warning"};
        for (int i = 0; i < 3; i++) {
            String routingKey = logLevel[i % 3];
            String message = "Hello RabbitMQ" + (i + 1);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        }
        channel.close();
        connection.close();
    }
}
