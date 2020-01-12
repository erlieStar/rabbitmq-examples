# RabbitMQ入门教程

## rabbitmq-api（rabbitmq api的使用）

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

需要注意的地方如下：
1. 当想做广播的时候可以用fanout类型的交换器，因为发送到该交换机的消息都会路由到与该交换机绑定的所有队列上
2. 当队列有多个消费者时，队列收到的消息将以轮询（round-robin）的方式发送给消费者

|BindingKey| 能够匹配到的RoutingKey |
|:--:|:--:|
| java.# | java.lang，java.util， java.util.concurrent|
|java.*|java.lang，java.util|
|\*.\*.uti|com.javashitang.util，org.spring.util|

### chapter_3: 拉取消息

消息的获得方式有2种

1. 拉取消息（get message）

2. 推送消息（consume message）

那我们应该拉取消息还是推送消息？get是一个轮询模型，而consumer是一个推送模型。get模型会导致每条消息都会产生与RabbitMQ同步通信的开销，这一个请求由发送请求帧的客户端应用程序和发送应答的RabbitMQ组成。所以推送消息，避免拉取

### chapter_4: 手动ack

消息的确认方式有2种

1. 自动确认（autoAck=true）
2. 手动确认（autoAck=false）

消费者在消费消息的时候，可以指定autoAck参数

String basicConsume(String queue, boolean autoAck, Consumer callback)

autoAck=false: RabbitMQ会等待消费者显示回复确认消息后才从内存（或者磁盘）中移出消息

autoAck=true: RabbitMQ会自动把发送出去的消息置为确认，然后从内存（或者磁盘）中删除，而不管消费者是否真正的消费了这些消息

手动确认的方法如下，有2个参数

basicAck(long deliveryTag, boolean multiple)

deliveryTag: 用来标识信道中投递的消息。RabbitMQ 推送消息给Consumer时，会附带一个deliveryTag，以便Consumer可以在消息确认时告诉RabbitMQ到底是哪条消息被确认了。

RabbitMQ保证在每个信道中，每条消息的deliveryTag从1开始递增

multiple=true: 消息id<=deliveryTag的消息，都会被确认

myltiple=false: 消息id=deliveryTag的消息，都会被确认

消息一直不确认会发生啥？

如果队列中的消息发送到消费者后，消费者不对消息进行确认，那么消息会一直留在队列中，直到确认才会删除。
如果发送到A消费者的消息一直不确认，只有等到A消费者与rabbitmq的连接中断，rabbitmq才会考虑将A消费者未确认的消息重新投递给另一个消费者

### chapter_5: 拒绝消息的两种方式

确认消息只有一种方法

1. basicAck(long deliveryTag, boolean multiple)

而拒绝消息有两种方式

1. basicNack(long deliveryTag, boolean multiple, boolean requeue)

2. basicReject(long deliveryTag, boolean requeue)

basicNack和basicReject的区别只有一个basicNack支持批量拒绝

deliveryTag和multiple参数前面已经说过。

requeue=true: 消息会被再次发送到队列中

requeue=false: 消息会被直接丢失

### chapter_6: 失败通知

当消息不能被路由到某个queue时，我们如何获取到不能正确路由的消息呢？

1. 在发送消息时设置mandatory为true
2. 生产者可以通过调用channel.addReturnListener来添加ReturnListener监听器获取没有被路由到队列中的消息

mandatory是channel.basicPublish()方法中的参数

mandatory=true: 交换器无法根据路由键找到一个符合条件的队列，那么RabbitMQ会调用Basic.Return命令将消息返回给生产者

mandatory=false: 出现上述情形，则消息直接被丢弃

### chapter_7: 发布者确认

### chapter_8: 备用交换器

生产者在发送消息的时候如果不设置 mandatory 参数那么消息在未被路由到queue的情况下将会丢失，如果设置了 mandatory 参数，那么需要添加 ReturnListener 的编程逻辑，生产者的代码将变得复杂。如果既不想复杂化生产者的编程逻辑，又不想消息丢失，那么可以使用备用交换器，这样可以将未被路由到queue的消息存储在RabbitMQ 中，在需要的时候去处理这些消息

### chapter_9: 事务

RabbitMQ中与事务机制相关的方法有3个

| 方法 | 解释 |
|--|--|
|channel.txSelect()  | 将当前的信道设置成事务模式|
|channel.txCommit()|提交事务|
|channel.txRollback()|回滚事务|

如果事务提交成功，则消息一定到达了RabbitMQ中
因为事务会榨干RabbitMQ的性能，所以一般使用发布者确认代替事务

### chapter_10: 消息持久化

### chapter_11: 死信队列

DLX，全称为Dead-Letter-Exchange，称之为死信交换器。当一个消息在队列中变成死信（dead message）之后，它能被重新发送到另一个交换器中，这个交换器就是DLX，绑定DLX的队列就称之为死信队列。
DLX也是一个正常的交换器，和一般的交换器没有区别，实际上就是设置某个队列的属性

**消息变成死信一般是由于以下几种情况**

1. 消息被拒绝（Basic.Reject/Basic.Nack）且不重新投递（requeue=false）
2. 消息过期
3. 队列达到最大长度

**死信交换器和备用交换器的区别**

备用交换器：1.消息无法路由时转到备用交换器 2.备用交换器是在声明主交换器的时候定义的
死信交换器：1.消息已经到达队列，但是被消费者拒绝等的消息会转到死信交换器。2.死信交换器是在声明队列的时候定义的

### chapter_12: 流量控制（服务质量保证）

**使用qos只要进行如下2个步骤即可**
 
1. autoAck设置为false（autoAck=true的时候不生效）
 
2. 调用basicConsume方法前先调用basicQos方法，这个方法有3个参数
 
basicQos(int prefetchSize, int prefetchCount, boolean global)
 
|参数名| 含义 |
|:--:|:--:|
|prefetchSize|批量取的消息的总大小，0为不限制|
|prefetchCount|消费完prefetchCount条（prefetchCount条消息被ack）才再次推送|
|global||
 
**为什么要使用qos?**
 
1. 提高服务稳定性。假设消费端有一段时间不可用，导致队列中有上万条未处理的消息，如果开启客户端，
巨量的消息推送过来，可能会导致消费端变卡，也有可能直接不可用，所以服务端限流很重要
 
2. 提高吞吐量。当队列有多个消费者时，队列收到的消息以轮询的方式发送给消费者。但由于机器性能等的原因，每个消费者的消费能力不一样，
这就会导致一些消费者处理完了消费的消息，而另一些则还堆积了一些消息，会造成整体应用吞吐量的下降

## springboot-rabbitmq（springboot整合rabbitmq）

## 联系我

email: erlie139@gmail.com

欢迎大家和我交流，关注公众号**Java识堂**获取我的联系方式
 
![欢迎fork和star](https://img-blog.csdnimg.cn/20200102100200903.jpg)
