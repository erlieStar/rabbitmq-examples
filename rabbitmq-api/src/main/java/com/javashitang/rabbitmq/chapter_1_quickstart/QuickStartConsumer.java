package com.javashitang.rabbitmq.chapter_1_quickstart;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class QuickStartConsumer {

    public static void main(String[] args) throws Exception {

        // 1.创建一个ConnectionFactory，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.javashitang.com");
        connectionFactory.setPort(5672);
        // 不设置的话，默认也为/
        connectionFactory.setVirtualHost("/");

        // 2.通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        // 3.通过connection创建一个channel
        Channel channel = connection.createChannel();

        String queueName = "focusError";
        String bindingKey = "error";
        // 5.声明交换器
        channel.exchangeDeclare(QuickStartProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        /**
         * 6.声明（创建）一个队列
         * queue: 队列名字
         * durable: 是否持久化
         * exclusive: 是否独占
         * autoDelete: 队列脱离exchange，自动删除
         * arguments: 扩展参数
         */
        channel.queueDeclare(queueName, true, false, false ,null);
        // 7.绑定交换机和队列
        channel.queueBind(queueName, QuickStartProducer.EXCHANGE_NAME, bindingKey);

        // 8.创建消费者
        Consumer quickStartConsumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey() ,message);
            }
        };

        // 9.消费者开始消费数据
        channel.basicConsume(queueName , true, quickStartConsumer);

    }
}
