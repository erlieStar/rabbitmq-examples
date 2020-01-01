package com.javashitang.rabbitmq.chapter_2_exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer4DirectExchange {

    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.javashitang.com");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        // 声明一个交换机
        channel.exchangeDeclare(Producer4DirectExchange.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "info_queue";
        String bindingKey = "info";
        // 队列名
        // durable 设置是否持久化。为true则设置队列为持久化。持久化的队列会存盘
        // exclusive 设置是否排他。为true则设置队列为排他的，如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除
        // autoDelete 设置是否自动删除。为true则设置队列为自动删除
        // arguments 设置队列的其他一些参数
        channel.queueDeclare(queueName, false, false, false ,null);
        channel.queueBind(queueName, Producer4DirectExchange.EXCHANGE_NAME, bindingKey);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("routingKey is " + envelope.getRoutingKey() + " message is "  + message);
            }
        };

        channel.basicConsume(queueName , true, consumer);

    }
}
