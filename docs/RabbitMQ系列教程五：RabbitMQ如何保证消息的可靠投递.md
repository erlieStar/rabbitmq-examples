![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007180949410.jpg?)
## 介绍
一个消息往往会经历如下几个阶段
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201008181112794.jpeg?)
所以要保证消息的可靠投递，只需要保证这3个阶段的可靠投递即可

## 生产阶段
这个阶段的可靠投递主要靠ConfirmListener（发布者确认）和ReturnListener（失败通知）
前面已经介绍过了，一条消息在RabbitMQ中的流转过程为
producer -> rabbitmq broker cluster -> exchange -> queue -> consumer

**ConfirmListener可以获取消息是否从producer发送到broker**
**ReturnListener可以获取从exchange路由不到queue的消息**

我用Spring Boot Starter 的api来演示一下效果

application.yaml
```yaml
spring:
  rabbitmq:
    host: myhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    listener:
      simple:
        acknowledge-mode: manual # 手动ack，默认为auto

log:
  exchange: log.exchange
  info:
    queue: info.log.queue
    binding-key: info.log.key
```

发布者确认回调
```java
@Component
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private MessageSender messageSender;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String msgId = correlationData.getId();
        String msg = messageSender.dequeueUnAckMsg(msgId);
        if (ack) {
            System.out.println(String.format("消息 {%s} 成功发送给mq", msg));
        } else {
        	// 可以加一些重试的逻辑
            System.out.println(String.format("消息 {%s} 发送mq失败", msg));
        }
    }
}
```

失败通知回调
```java
@Component
public class ReturnCallback implements RabbitTemplate.ReturnCallback {

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msg = new String(message.getBody());
        System.out.println(String.format("消息 {%s} 不能被正确路由，routingKey为 {%s}", msg, routingKey));
    }
}
```

```java
@Configuration
public class RabbitMqConfig {

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         ReturnCallback returnCallback, ConfirmCallback confirmCallback) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 要想使 returnCallback 生效，必须设置为true
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }
}
```
这里我对RabbitTemplate做了一下包装，主要就是发送的时候增加消息id，并且保存消息id和消息的对应关系，因为RabbitTemplate.ConfirmCallback只能拿到消息id，并不能拿到消息内容，所以需要我们自己保存这种映射关系。在一些可靠性要求比较高的系统中，你可以将这种映射关系存到数据库中，成功发送删除映射关系，失败则一直发送

```java
@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public final Map<String, String> unAckMsgQueue = new ConcurrentHashMap<>();

    public void convertAndSend(String exchange, String routingKey, String message) {
        String msgId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(msgId);
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
        unAckMsgQueue.put(msgId, message);
    }

    public String dequeueUnAckMsg(String msgId) {
        return unAckMsgQueue.remove(msgId);
    }

}
```
测试代码为

```java
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
```

**先来测试失败者通知**

输出为
```text
消息 {this is error message 0} 不能被正确路由，routingKey为 {test}
消息 {this is error message 0} 成功发送给mq
消息 {this is error message 2} 不能被正确路由，routingKey为 {test}
消息 {this is error message 2} 成功发送给mq
消息 {this is error message 1} 不能被正确路由，routingKey为 {test}
消息 {this is error message 1} 成功发送给mq
```
**消息都成功发送到broker，但是并没有被路由到queue中**

**再来测试发布者确认**

输出为

```text
消息 {this is info message 0} 成功发送给mq
infoLogQueue 收到的消息为: {this is info message 0}
infoLogQueue 收到的消息为: {this is info message 1}
消息 {this is info message 1} 成功发送给mq
infoLogQueue 收到的消息为: {this is info message 2}
消息 {this is info message 2} 成功发送给mq
```

**消息都成功发送到broker，也成功被路由到queue中**

## 存储阶段
这个阶段的高可用还真没研究过，毕竟集群都是运维搭建的，后续有时间的话会把这快的内容补充一下

## 消费阶段
消费阶段的可靠投递主要靠ack来保证。
前文已经介绍了原生api ack的方式和Spring Boot框架ack的方式

总而言之，在生产环境中，我们一般都是**单条手动ack**，消费失败后不会重新入队（因为很大概率还会再次失败），而是将消息重新投递到死信队列，方便以后排查问题

**总结一下各种情况**

1. ack后消息从broker中删除
2. nack或者reject后，分为如下2种情况
   (1) reque=true，则消息会被重新放入队列
   (2) reque=fasle，消息会被直接丢弃，如果指定了死信队列的话，会被投递到死信队列

**消息一直不确认会发生啥？**

如果队列中的消息发送到消费者后，消费者不对消息进行确认，那么消息会一直留在队列中，直到确认才会删除。
如果发送到A消费者的消息一直不确认，只有等到A消费者与rabbitmq的连接中断，rabbitmq才会考虑将A消费者未确认的消息重新投递给另一个消费者