package com.javashitang.rabbitmq.chapter_3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.GetResponse;

public class GetMessageConsumer {

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.erlie.cc");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false ,null);

        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel);

        channel.basicConsume(queueName , true, consumer);

        while (true) {
            // 7.获取消息
            GetResponse getResponse = channel.basicGet(queueName, true);
            String msg = new String(getResponse.getBody());
            System.out.println("消费端 " + msg);
        }
    }
}
