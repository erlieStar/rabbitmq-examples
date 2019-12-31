package com.javashitang.rabbitmq.chapter_10_qos;

import com.javashitang.rabbitmq.chapter_9_msgDurable.MsgDurableProducer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerMain {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("www.javashitang.com");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(MsgDurableProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);

		String queueName = "msg_durable";
		channel.queueDeclare(queueName, true, false, false, null);

		channel.queueBind(queueName, MsgDurableProducer.EXCHANGE_NAME, "error");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(envelope.getRoutingKey() + " " + message);
			}
		};
		// 需要注意的地方
		// 1.autoAck设置为false
		// 2.调用basicConsume方法前先调用basicQos方法，prefetchSize=0为不限制消息大小
		// prefetchCount为消费完3条（3条消息被ack）才开始开始推送，
		// false
		channel.basicQos(0, 3 ,false);
		channel.basicConsume(queueName, false, consumer);
	}
}
