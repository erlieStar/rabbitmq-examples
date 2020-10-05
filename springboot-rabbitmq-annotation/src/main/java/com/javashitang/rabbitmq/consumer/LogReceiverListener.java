package com.javashitang.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: lilimin
 * @Date: 2019/12/28 13:28
 */
@Slf4j
@Component
public class LogReceiverListener {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${info.log.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${info.log.key}"
            )
    )
    @RabbitHandler
    public void infoLog(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        try {
            System.out.println("infoLogQueue 收到的消息为 " + msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${all.log.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${all.log.key}"
            )
    )
    @RabbitHandler
    public void allLog(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        try {
            System.out.println("allLogQueue 收到的消息为 " + msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
