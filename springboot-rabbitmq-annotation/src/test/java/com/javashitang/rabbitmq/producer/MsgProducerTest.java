package com.javashitang.rabbitmq.producer;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgProducerTest {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("${log.exchange}")
    private String exchange;
    @Value("${log.info.binding-key}")
    private String routingKey;

    @SneakyThrows
    @Test
    public void sendMsg() {
        for (int i = 0; i < 5; i++) {
            String message = "this is info message " + i;
            amqpTemplate.convertAndSend(exchange, routingKey, message);
        }

        System.in.read();
    }
}
