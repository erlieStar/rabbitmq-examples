package com.javashitang.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @Author: lilimin
 * @Date: 2019/12/28 13:28
 *
 * 通过 RabbitMqConfig 设置，消息将被这个类接收
 */
@Component
public class LogReceiverListener implements ChannelAwareMessageListener {

    /**
     * 发送消息的生产者在测试包中 MsgProducerTest
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        System.out.println("收到的消息为 " + msg);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
