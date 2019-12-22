package com.javashitang.rabbitmq.chapter_2.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.GetResponse;

public class Consumer4TopicExchange {

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.erlie.cc");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // 支持自动重连，每3秒连接一次
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String exchangeType = "topic";
        String queueName = "test_topic_queue";
        String routingKey = "user.#";

        // 声明一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        // 声明一个队列
        channel.queueDeclare(queueName, false, false, false ,null);
        // 建立一个绑定
        channel.queueBind(queueName, exchangeName, routingKey);

        Consumer consumer = new DefaultConsumer(channel);

        channel.basicConsume(queueName , true, consumer);

        while (true) {
            // 7.获取消息
            GetResponse getResponse = channel.basicGet(queueName, true);
            String msg = new String(getResponse.getBody());
            System.out.println("消费端 " + msg);
        }
    }
}
