package com.javashitang.rabbitmq.consumer;

import com.javashitang.rabbitmq.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lilimin
 * @since 2020-10-06
 */
@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public final Map<String, String> unAckMsgQueue = new ConcurrentHashMap<>();

    public void convertAndSend(String message) {
        String msgId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(msgId);
        rabbitTemplate.convertAndSend(RabbitMqConfig.LOG_EXCHANGE, RabbitMqConfig.LOG_ALL_BINDING_KEY, message, correlationData);
        unAckMsgQueue.put(msgId, message);
    }

    public String dequeueUnAckMsg(String msgId) {
        return unAckMsgQueue.remove(msgId);
    }

}
