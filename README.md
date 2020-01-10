# RabbitMQ入门教程

## rabbitmq-api（rabbitmq api的适用）

### chapter_1: 快速开始，手写一个RabbitMQ的生产者和消费者

### chapter_2: 演示了各种exchange的使用

|交换器类型|路由规则|
|:--:|:--:|
|fanout|发送到该交换机的消息都会路由到与该交换机绑定的所有队列上，可以用来做广播|
|direct|把消息路由到BindingKey和RoutingKey完全匹配的队列中|
|topic|topic和direct类似，也是将消息发送到RoutingKey和BindingKey相匹配的队列中，只不过可以模糊匹配|
|headers|性能差，基本不会使用|

1. RoutinKey为一个被“.”号分割的字符串（如com.rabbitmq.client）
2. BindingKey和RoutingKey也是“.”号分割的字符串
3. BindKey中可以存在两种特殊字符串“*”和“#”，用于做模糊匹配，其中“\*”用于匹配不多不少一个词，“#”用于匹配多个单词（包含0个，1个）


|BindingKey| 能够匹配到的RoutingKey |
|:--:|:--:|
| java.# | java.lang，java.util， java.util.concurrent|
|java.*|java.lang，java.util|
|\*.\*.uti|com.javashitang.util，org.spring.util|

### chapter_3: 拉取消息

消息的获得方式有2种

1.拉取消息（get message）

2.推送消息（consume message）

那我们应该拉取消息还是推送消息？get是一个轮询模型，而consumer是一个推送模型。get模型会导致每条消息都会产生与RabbitMQ同步通信的开销，这一个请求由发送请求帧的客户端应用程序和发送应答的RabbitMQ组成。所以推送消息，避免拉取

### chapter_4: 手动ack

### chapter_5: 拒绝消息的两种方式

### chapter_6: 失败通知

### chapter_7: 发布者确认

### chapter_8: 备用交换器

### chapter_9: 事务

### chapter_10: 消息持久化

### chapter_11: 死信队列

### chapter_12: 流量控制（服务质量保证）

## springboot-rabbitmq（springboot整合rabbitmq）

![欢迎fork和star](https://github.com/erlieStar/image/blob/master/%E6%AC%A2%E8%BF%8Efork%E5%92%8Cstar.jpg)