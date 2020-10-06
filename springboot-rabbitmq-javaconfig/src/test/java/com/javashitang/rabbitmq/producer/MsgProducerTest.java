package com.javashitang.rabbitmq.producer;

import com.javashitang.rabbitmq.consumer.MessageSender;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgProducerTest {

    @Autowired
    private MessageSender messageSender;

    @SneakyThrows
    @Test
    public void sendMsg() {
        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq " + i;
            messageSender.convertAndSend(message);
        }

        System.in.read();
    }
}
