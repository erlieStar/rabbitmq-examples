package com.javashitang.rabbitmq.chapter_7_publisherConfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AsyncConfirmProducer {

		public final static String EXCHANGE_NAME = "publisher_confirm_exchange";

		public static void main(String[] args) throws IOException, TimeoutException {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("www.javashitang.com");

				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

				// 启用发布者确认模式
				channel.confirmSelect();

				channel.addConfirmListener(new ConfirmListener() {
						@Override
						public void handleAck(long deliveryTag, boolean multiple) throws IOException {

						}
						@Override
						public void handleNack(long deliveryTag, boolean multiple)
								throws IOException {

						}
				});

				String[] logLevel = {"error", "warning"};

				for (int i = 0; i < 10; i++) {
						String routingKey = logLevel[i % 2];
						String message = "hello rabbit " + i;
						channel.basicPublish(EXCHANGE_NAME, routingKey, true, null, message.getBytes());
				}

				channel.close();
				connection.close();
		}
}
