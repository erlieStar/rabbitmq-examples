package com.javashitang.rabbitmq.chapter_1_quickstart;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QuickStartProducer {

    public final static String EXCHANGE_NAME  = "direct_logs";

    public static void main(String[] args) throws Exception {

        // 1.创建一个ConnectionFactory，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.javashitang.com");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2.通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        // 3.通过connection创建一个channel
        Channel channel = connection.createChannel();

        // 4.创建交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 5.通过channel发送数据
        for (int i = 0; i < 5; i++) {
            String msg = "hello RabbitMQ";
            channel.basicPublish(EXCHANGE_NAME, "test001", null, msg.getBytes());
        }

        // 6.记得要关闭相关的连接
        channel.close();
        connection.close();
    }
}
