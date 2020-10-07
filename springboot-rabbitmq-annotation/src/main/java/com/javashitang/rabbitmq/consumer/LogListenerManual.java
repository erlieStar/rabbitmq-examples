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
 *
 * 消息ack的方式为MANUAL（默认的ack方式）
 * 用户需要手动发送ack或者nack
 */
@Slf4j
@Component
public class LogListenerManual {

    /**
     * 接收info级别的日志
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${log.info.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${log.info.binding-key}"
            )
    )
    @RabbitHandler
    public void infoLog(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("infoLogQueue 收到的消息为: {}", msg);
        try {
            // 这里写各种业务逻辑
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    /**
     * 接收所有的日志
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${log.all.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${log.all.binding-key}"
            )
    )
    @RabbitHandler
    public void allLog(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("allLogQueue 收到的消息为: {}", msg);
        try {
            // 这里写各种业务逻辑
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
