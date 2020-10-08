package com.javashitang.config;

import com.javashitang.consumer.MessageSender;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lilimin
 * @since 2020-10-06
 *
 * 用来判断消息是否被ack
 * ack true for ack, false for nack
 */
@Component
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private MessageSender messageSender;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String msgId = correlationData.getId();
        String msg = messageSender.dequeueUnAckMsg(msgId);
        if (ack) {
            System.out.println(String.format("消息 {%s} 成功发送给mq", msg));
        } else {
            System.out.println(String.format("消息 {%s} 发送mq失败", msg));
        }
    }
}
