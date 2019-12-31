package com.javashitang.rabbitmq.config;

import com.javashitang.rabbitmq.amqp.LogReceiverListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lilimin
 * @Date: 2019/12/28 12:21
 */
@Configuration
public class RabbitMqConfig {

    public static final String LOG_QUEUE = "logQueue";
    public static final String LOG_EXCHANGE = "logExchange";

    // ====> declare connectionFactorys <===
    @Bean("msgConnectionFactory")
    public ConnectionFactory msgConnectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        return connectionFactory;
    }

    // ====> declare queues <===
    @Bean(LOG_QUEUE)
    public Queue queueA1() {
        return new Queue(LOG_QUEUE, true);
    }

    // ====> declare exchanges <===
    @Bean(LOG_EXCHANGE)
    public DirectExchange exchangeA1() {
        return new DirectExchange(LOG_EXCHANGE);
    }

    // ====> declare bindings <===
    @Bean
    public Binding bindingA1(@Qualifier(LOG_QUEUE) Queue queue,
                             @Qualifier(LOG_EXCHANGE) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LOG_QUEUE);
    }
    // ====> declare containers <===

    @Bean
    public SimpleMessageListenerContainer container(
            @Qualifier("msgConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier(LOG_QUEUE) Queue q1,
            LogReceiverListener logReceiverListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(q1);
        container.setMaxConcurrentConsumers(15);
        container.setConcurrentConsumers(15);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(logReceiverListener);
        return container;
    }
    // ====> declare templates <===
    public AmqpTemplate msgRabbitTemplate(@Qualifier("msgConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    // 失败通知
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return new RabbitTemplate.ReturnCallback() {

            @Override
            public void returnedMessage(Message message, int replyCode, String replyText,
                String exchange, String routingKey) {
                System.out.println("无法正确路由的消息，需要考虑另行处理");
            }
        };
    }

    // 发布者确认
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    System.out.println("消息成功发送给mq");
                } else {
                    System.out.println("消息发送mq失败");
                }
            }
        };
    }
}
