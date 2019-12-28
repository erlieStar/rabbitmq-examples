package com.javashitang.rabbitmq.chapter_3_getmessage;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lilimin
 * @Date: 2019/8/26 23:30
 */
public class GetMessageConsumer {


    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.javashitang.com");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(GetMessageProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, GetMessageProducer.EXCHANGE_NAME, "error");

        while(true) {
            GetResponse getResponse = channel.basicGet(queueName, true);
            if (null != getResponse) {
                System.out.println(getResponse.getEnvelope().getRoutingKey() + " " + new String(getResponse.getBody()));
            }
            Thread.sleep(1000);
        }
    }
}
