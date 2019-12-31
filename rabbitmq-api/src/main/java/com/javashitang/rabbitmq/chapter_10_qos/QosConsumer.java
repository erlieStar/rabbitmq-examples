package com.javashitang.rabbitmq.chapter_10_qos;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @Author: lilimin
 * @Date: 2019/12/31 23:09
 */
public class QosConsumer extends DefaultConsumer {

    public QosConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        this.getChannel().basicAck(envelope.getDeliveryTag(), true);
    }
}
