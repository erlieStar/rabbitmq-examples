package com.javashitang.producer;

import com.javashitang.consumer.MessageSender;
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
    private MessageSender messageSender;
    @Value("${log.exchange}")
    private String exchange;
    @Value("${log.info.binding-key}")
    private String routingKey;

    /**
     * 测试失败通知
     */
    @SneakyThrows
    @Test
    public void sendErrorMsg() {
        for (int i = 0; i < 3; i++) {
            String message = "this is error message " + i;
            messageSender.convertAndSend(exchange, "test", message);
        }
        System.in.read();
    }

    /**
     * 测试发布者确认
     */
    @SneakyThrows
    @Test
    public void sendInfoMsg() {
        for (int i = 0; i < 3; i++) {
            String message = "this is info message " + i;
            messageSender.convertAndSend(exchange, routingKey, message);
        }
        System.in.read();
    }
}
