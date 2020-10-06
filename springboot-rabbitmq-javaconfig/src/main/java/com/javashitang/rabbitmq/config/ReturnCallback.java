package com.javashitang.rabbitmq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lilimin
 * @since 2020-10-06
 *
 * 当消息不能被正确路由到某个queue时，会回调如下方法
 */
@Component
public class ReturnCallback implements RabbitTemplate.ReturnCallback {

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("无法正确路由的消息，需要考虑另行处理");
    }
}
