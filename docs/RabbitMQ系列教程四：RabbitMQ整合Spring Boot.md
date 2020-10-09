![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100718035925.jpg?)
## 介绍
github地址：
https://github.com/erlieStar/rabbitmq-examples

Spring有三种配置方式
1. 基于XML
2. 基于JavaConfig
3. 基于注解

当然现在已经很少使用XML来做配置了，只介绍一下用JavaConfig和注解的配置方式

RabbitMQ整合Spring Boot，我们只需要增加对应的starter即可

```xml
 <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-amqp</artifactId>
 </dependency>
```

## 基于注解
在application.yaml的配置如下
```yaml
spring:
  rabbitmq:
    host: myhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /

log:
  exchange: log.exchange
  info:
    queue: info.log.queue
    binding-key: info.log.key
  error:
    queue: error.log.queue
    binding-key: error.log.key
  all:
    queue: all.log.queue
    binding-key: '*.log.key'
```

消费者代码如下

```java
@Slf4j
@Component
public class LogReceiverListener {

    /**
     * 接收info级别的日志
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${log.info.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${log.info.binding-key}"
            )
    )
    public void infoLog(Message message) {
        String msg = new String(message.getBody());
        log.info("infoLogQueue 收到的消息为: {}", msg);
    }

    /**
     * 接收所有的日志
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${log.all.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${log.all.binding-key}"
            )
    )
    public void allLog(Message message) {
        String msg = new String(message.getBody());
        log.info("allLogQueue 收到的消息为: {}", msg);
    }
}
```
生产者如下

```java
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
```

Spring Boot针对消息ack的方式和原生api针对消息ack的方式有点不同

### 原生api消息ack的方式
**消息的确认方式有2种**

自动确认（autoAck=true）
手动确认（autoAck=false）

**消费者在消费消息的时候，可以指定autoAck参数**

String basicConsume(String queue, boolean autoAck, Consumer callback)

autoAck=false: RabbitMQ会等待消费者显示回复确认消息后才从内存（或者磁盘）中移出消息

autoAck=true: RabbitMQ会自动把发送出去的消息置为确认，然后从内存（或者磁盘）中删除，而不管消费者是否真正的消费了这些消息

**手动确认的方法如下，有2个参数**

basicAck(long deliveryTag, boolean multiple)

deliveryTag: 用来标识信道中投递的消息。RabbitMQ 推送消息给Consumer时，会附带一个deliveryTag，以便Consumer可以在消息确认时告诉RabbitMQ到底是哪条消息被确认了。
RabbitMQ保证在每个信道中，每条消息的deliveryTag从1开始递增

multiple=true: 消息id<=deliveryTag的消息，都会被确认

myltiple=false: 消息id=deliveryTag的消息，都会被确认

**消息一直不确认会发生啥？**

如果队列中的消息发送到消费者后，消费者不对消息进行确认，那么消息会一直留在队列中，直到确认才会删除。
如果发送到A消费者的消息一直不确认，只有等到A消费者与rabbitmq的连接中断，rabbitmq才会考虑将A消费者未确认的消息重新投递给另一个消费者

## Spring Boot中针对消息ack的方式

有三种方式，定义在AcknowledgeMode枚举类中

| 方式   | 解释                                                         |
| ------ | ------------------------------------------------------------ |
| NONE   | 没有ack，等价于原生api中的autoAck=true                       |
| MANUAL | 用户需要手动发送ack或者nack                                  |
| AUTO   | 方法正常结束，spring boot 框架返回ack，发生异常spring boot框架返回nack |

spring boot针对消息默认的ack的方式为AUTO。

在实际场景中，我们一般都是手动ack。

application.yaml的配置改为如下

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
```
相应的消费者代码改为

```java
@Slf4j
@Component
public class LogListenerManual {

    /**
     * 接收info级别的日志
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${log.info.queue}", durable = "true"),
                    exchange = @Exchange(value = "${log.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${log.info.binding-key}"
            )
    )
    public void infoLog(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("infoLogQueue 收到的消息为: {}", msg);
        try {
            // 这里写各种业务逻辑
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
```

我们上面用到的注解，作用如下

| 注解           | 作用                                                         |
| -------------- | ------------------------------------------------------------ |
| RabbitListener | 消费消息，可以定义在类上，方法上，当定义在类上时需要和RabbitHandler配合使用 |
| QueueBinding   | 定义绑定关系                                                 |
| Queue          | 定义队列                                                     |
| Exchange       | 定义交换机                                                   |
| RabbitHandler  | RabbitListener定义在类上时，需要用RabbitHandler指定处理的方法 |
## 基于JavaConfig
既然用注解这么方便，为啥还需要JavaConfig的方式呢？
JavaConfig方便自定义各种属性，比如同时配置多个virtual host等

具体代码看GitHub把
