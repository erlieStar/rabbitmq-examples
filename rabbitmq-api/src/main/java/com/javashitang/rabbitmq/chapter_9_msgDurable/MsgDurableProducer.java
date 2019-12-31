package com.javashitang.rabbitmq.chapter_9_msgDurable;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MsgDurableProducer {

	public static final String EXCHANGE_NAME = "msg_durable";

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("www.javashitang.com");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

		String[] logLevel = {"error", "warning"};
		// 将当前信道设置成事务模式
		channel.txSelect();
		for (int i = 0; i < 10; i++) {
			String routingKey = logLevel[i % 2];
			String message = String.format("hello rabbit %s", i);
			channel.basicPublish(EXCHANGE_NAME, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		}
		channel.close();
		connection.close();
	}
}
