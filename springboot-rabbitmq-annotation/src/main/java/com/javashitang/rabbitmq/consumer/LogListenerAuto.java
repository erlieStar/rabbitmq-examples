package com.javashitang.rabbitmq.consumer;

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
 * 消息ack的方式为AUTO
 * 方法正常结束，spring boot 框架返回ack，发生异常spring boot框架返回nack
 */
@Slf4j
@Component
public class LogListenerAuto {

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
    public void infoLog(Message message) {
        String msg = new String(message.getBody());
        log.info("infoLogQueue 收到的消息为: {}", msg);
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
    public void allLog(Message message) {
        String msg = new String(message.getBody());
        log.info("allLogQueue 收到的消息为: {}", msg);
    }
}
