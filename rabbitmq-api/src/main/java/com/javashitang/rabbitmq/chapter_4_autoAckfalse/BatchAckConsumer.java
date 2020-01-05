package com.javashitang.rabbitmq.chapter_4_autoAckfalse;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 这个是一个批量确认的demo
 */
@Slf4j
public class BatchAckConsumer extends DefaultConsumer {

    private int count = 0;

    public BatchAckConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        count++;
        if (count == Integer.MAX_VALUE) {
            count = 0;
        }
        if (count % 50 == 0) {
            this.getChannel().basicAck(envelope.getDeliveryTag(), true);
        }
        log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey() ,message);
    }
}
