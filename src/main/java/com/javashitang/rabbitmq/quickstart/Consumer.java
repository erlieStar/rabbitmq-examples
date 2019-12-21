package com.javashitang.rabbitmq.quickstart;

import com.rabbitmq.client.*;

public class Consumer {

    public static void main(String[] args) throws Exception {

        // 1.创建一个ConnectionFactory，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.erlie.cc");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 2.通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        // 3.通过connection创建一个channel
        Channel channel = connection.createChannel();

        // 4.声明（创建）一个队列
        // queue:队列名字
        // durable:是否持久化
        // exclusive:是否独占
        // autoDelete:队列脱离exchange，自动删除
        // arguments:扩展参数
        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false ,null);

        // 5.创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        // 6.设置Channel
        channel.basicConsume(queueName , true, queueingConsumer);

        while (true) {
            // 7.获取消息
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("消费端 " + msg);
        }


    }
}
