package com.javashitang.rabbitmq.enjoy.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:52
 */
public class NormalConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.erlie.cc");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, "direct");

        String queueName = "focuserror";
        // 队列名
        // durable 设置是否持久化。为true则设置队列为持久化。持久化的队列会存盘
        // exclusive 设置是否排他。为true则设置队列为排他的，如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除
        // autoDelete 设置是否自动删除。为true则设置队列为自动删除
        // arguments 设置队列的其他一些参数
        channel.queueDeclare(queueName, false, false, false ,null);

        String bindingKey = "info";
        channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, bindingKey);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(envelope.getRoutingKey() + message);
            }
        };
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName, true, consumer);
    }
}
