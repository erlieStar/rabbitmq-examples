package com.javashitang.rabbitmq.producer;

import ch.qos.logback.core.util.TimeUtil;
import com.javashitang.rabbitmq.config.RabbitMqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgProducerTest {

		@Autowired
		AmqpTemplate amqpTemplate;

		@Test
		public void sendMsg() {
				for (int i = 0; i < 5; i++) {
						String message = "hello rabbitmq " + i;
						amqpTemplate.convertAndSend(RabbitMqConfig.LOG_QUEUE, message);
				}

				try {
						TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
		}
}
