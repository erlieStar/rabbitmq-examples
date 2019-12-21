package com.javashitang.rabbitmq.api.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class Producer {

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.erlie.cc");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        Map<String, Object> headers = new HashMap<>();
        headers.put("my1", "111");
        headers.put("my2", "222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)  // 持久化
                .contentEncoding("UTF-8") // 字符集
                .expiration("10000")    // 过期时间
                .headers(headers)
                .build();

        for (int i = 0; i < 5; i++) {
            String msg = "hello RabbitMQ";
            channel.basicPublish("", "test001", properties, msg.getBytes());
        }

        channel.close();
        connection.close();
    }
}
