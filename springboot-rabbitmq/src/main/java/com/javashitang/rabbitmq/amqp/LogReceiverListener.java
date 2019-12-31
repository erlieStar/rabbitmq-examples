package com.javashitang.rabbitmq.amqp;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @Author: lilimin
 * @Date: 2019/12/28 13:28
 */
@Component
public class LogReceiverListener implements ChannelAwareMessageListener {

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
