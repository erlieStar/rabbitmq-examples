# RabbitMQ入门教程

## 前言

我们先来看一下一条消息在RabbitMQ中的流转过程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191124104439425.jpeg?)

图示的主要流程如下
1. 生产者发送消息的时候指定RoutingKey，然后消息被发送到Exchange
2. Exchange根据一些列规则将消息路由到指定的队列中
3. 消费者从队列中消费消息

整个流程主要就4个参与者,message,exchange,queue,consumer，我们就来认识一下这4个参与者

### Message

消息可以设置一些列属性，每种属性的作用可以参考《深入RabbitMQ》一书

|属性名| 用处 |
|:--:|:--:|
| contentType | 消息体的MIME类型，如application/json |
| contentEncoding | 消息的编码类型，如是否压缩 |
| messageId |消息的唯一性标识，由应用进行设置  |
| correlationId | 一般用作关联消息的message-id，常用于消息的响应 |
| timestamp | 消息的创建时刻，整型，精确到秒 |
| expiration |  消息的过期时刻，字符串，但是呈现格式为整型，精确到秒|
| deliveryMode |消息的持久化类型 ，1为非持久化，2为持久化，性能影响巨大|
| appId | 应用程序的类型和版本号 |
| userId| 标识已登录用户，极少使用 |
| type | 消息类型名称，完全由应用决定如何使用该字段 |
| replyTo | 构建回复消息的私有响应队列 |
| headers | 键/值对表，用户自定义任意的键和值 |
| priority | 指定队列中消息的优先级 |

### Exchange

接收消息，并根据路由键转发消息到所绑定的队列，常用的属性如下

|交换机属性|类型|
|:-:|:-:|
|name|交换器名称|
|type|交换器类型，有如下四种，direct，topic，fanout，headers|
|durability|是否需要持久化，true为持久化。持久化可以将交换器存盘，在服务器重启的时候不会丢失相关信息|
|autoDelete|与这个Exchange绑定的Queue或Exchange都与此解绑时，会删除本交换器|
|internal|设置是否内置，true为内置。如果是内置交换器，客户端无法发送消息到这个交换器中，只能通过交换器路由到交换器这种方式|
|argument|其他一些结构化参数|

我们最常使用的就是type属性，下面就详细解释type属性

### Fanout Exchange

![这里写图片描述](https://img-blog.csdn.net/20180915221251946?)

**发送到该交换机的消息都会路由到与该交换机绑定的所有队列上，可以用来做广播**

不处理路由键，只需要简单的将队列绑定到交换机上	

Fanout交换机转发消息是最快的

### Direct Exchage

![这里写图片描述](https://img-blog.csdn.net/20180915221209957?)

**把消息路由到BindingKey和RoutingKey完全匹配的队列中**

### Topic Exchange

![这里写图片描述](https://img-blog.csdn.net/20180915221232488?)

前面说到，direct类型的交换器路由规则是完全匹配RoutingKey和BindingKey。topic和direct类似，也是将消息发送到RoutingKey和BindingKey相匹配的队列中，只不过可以模糊匹配。

1. RoutinKey为一个被“.”号分割的字符串（如com.rabbitmq.client）
2. BindingKey和RoutingKey也是“.”号分割的字符串
3. BindKey中可以存在两种特殊字符串“*”和“#”，用于做模糊匹配，其中“\*”用于匹配不多不少一个词，“#”用于匹配多个单词（包含0个，1个）

|BindIngKey| 能够匹配到的RoutingKey|
|:--:|:--:|
| java.# | java.lang，java.util， java.util.concurrent|
|java.*|java.lang，java.util|
|\*.\*.uti|com.javashitang.util，org.spring.util|

假如现在有2个RoutingKey为java.lang和java.util.concurrent的消息，java.lang会被路由到Consumer1和Consumer2，java.util.concurrent会被路由到Consumer2。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019122821511510.png)

### Headers Exchange

headers类型的交换器不依赖于路由键的匹配规则来路由消息，而是根据发送消息内容中的headers属性进行匹配。headers类型的交换器性能差，不实用，基本上不会使用。

### Queue

队列的常见属性如下

|参数名| 用处 |
|:--:|:--:|
| queue | 队列的名称 |
| durable| 是否持久化，true为持久化。持久化的队列会存盘，在服务器重启的时候可以保证不丢失相关信息 |
| exclusive|  设置是否排他，true为排他。如果一个队列被声明为排他队列，该队列仅对首次声明他它的连接可见，并在连接断开时自动删除（即一个队列只能有一个消费者）|
| autoDelete |  设置是否自动删除，true为自动删除，自动删除的前提是，至少一个消费者连接到这个队列，之后所有与这个连接的消费者都断开时，才会自动删除|
| arguments|  设置队列的其他参数，如x-message-ttl，x-max-length|

arguments中可以设置的队列的常见参数如下

|参数名|目的  |
|:--:|:--:|
| x-dead-letter-exchange |死信交换器 |
| x-dead-letter-routing-key |死信消息的可选路由键 |
|x-expires|队列在指定毫秒数后被删除|
|x-ha-policy|创建HA队列|
|x-ha-nodes|HA队列的分布节点|
|x-max-length|队列的最大消息数|
|x-message-ttl|毫秒为单位的消息过期时间，队列级别|
|x-max-priority|最大优先值为255的队列优先排序功能|

## rabbitmq-api（rabbitmq api的使用）

### chapter_1: 快速开始，手写一个RabbitMQ的生产者和消费者

### chapter_2: 演示了各种exchange的使用

来回顾一下上面说的各种exchange机器路由规则

|交换器类型|路由规则|
|:--:|:--:|
|fanout|发送到该交换机的消息都会路由到与该交换机绑定的所有队列上，可以用来做广播|
|direct|把消息路由到BindingKey和RoutingKey完全匹配的队列中|
|topic|topic和direct类似，也是将消息发送到RoutingKey和BindingKey相匹配的队列中，只不过可以模糊匹配|
|headers|性能差，基本不会使用|

### chapter_3: 拉取消息

**消息的获得方式有2种**

1. 拉取消息（get message）

2. 推送消息（consume message）

那我们应该拉取消息还是推送消息？get是一个轮询模型，而consumer是一个推送模型。get模型会导致每条消息都会产生与RabbitMQ同步通信的开销，这一个请求由发送请求帧的客户端应用程序和发送应答的RabbitMQ组成。所以推送消息，避免拉取

### chapter_4: 手动ack

**消息的确认方式有2种**

1. 自动确认（autoAck=true）
2. 手动确认（autoAck=false）

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

### chapter_5: 拒绝消息的两种方式

**确认消息只有一种方法**

1. basicAck(long deliveryTag, boolean multiple)

**而拒绝消息有两种方式**

1. basicNack(long deliveryTag, boolean multiple, boolean requeue)

2. basicReject(long deliveryTag, boolean requeue)

basicNack和basicReject的区别只有一个，basicNack支持批量拒绝

deliveryTag和multiple参数前面已经说过。

requeue=true: 消息会被再次发送到队列中

requeue=false: 消息会被直接丢失

### chapter_6: 失败通知

chapter_6到chapter_10主要简述了消息发布时的权衡

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191223235517386.png?)

**我们最常用的就是失败通知和发布者确认**

**当消息不能被路由到某个queue时，我们如何获取到不能正确路由的消息呢？**

1. 在发送消息时设置mandatory为true
2. 生产者可以通过调用channel.addReturnListener来添加ReturnListener监听器获取没有被路由到队列中的消息

mandatory是channel.basicPublish()方法中的参数

mandatory=true: 交换器无法根据路由键找到一个符合条件的队列，那么RabbitMQ会调用Basic.Return命令将消息返回给生产者

mandatory=false: 出现上述情形，则消息直接被丢弃

### chapter_7: 发布者确认

当消息被发送后，消息到底有没有到达exchange呢？默认情况下生产者是不知道消息有没有到达exchange

**RabbitMQ针对这个问题，提供了两种解决方式**
1. 事务（后面会讲到）
2. 发布者确认（publisher confirm）

**而发布者确认有三种编程方式**

1. 普通confirm模式：每发送一条消息后，调用waitForConfirms()方法，等待服务器端confirm。实际上是一种串行confirm了。
2. 批量confirm模式：每发送一批消息后，调用waitForConfirms()方法，等待服务器端confirm。
3. 异步confirm模式：提供一个回调方法，服务端confirm了一条或者多条消息后Client端会回调这个方法。

异步confirm模式的性能最高，因此经常使用，我想把这个分享的细一下

```
channel.addConfirmListener(new ConfirmListener() {
	@Override
	public void handleAck(long deliveryTag, boolean multiple) throws IOException {
		log.info("handleAck, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
	}

	@Override
	public void handleNack(long deliveryTag, boolean multiple) throws IOException {
		log.info("handleNack, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
	}
});
```

写过异步confirm代码的小伙伴应该对这段代码不陌生，可以看到这里也有deliveryTag和multiple。但是我要说的是这里的deliveryTag和multiple和消息的ack没有一点关系。

confirmListener中的ack: rabbitmq控制的，用来确认消息是否到达exchange

消息的ack: 上面说到可以自动确认，也可以手动确认，用来确认queue中的消息是否被consumer消费

### chapter_8: 备用交换器

生产者在发送消息的时候如果不设置 mandatory 参数那么消息在未被路由到queue的情况下将会丢失，如果设置了 mandatory 参数，那么需要添加 ReturnListener 的编程逻辑，生产者的代码将变得复杂。如果既不想复杂化生产者的编程逻辑，又不想消息丢失，那么可以使用备用交换器，这样可以将未被路由到queue的消息存储在RabbitMQ 中，在需要的时候去处理这些消息

### chapter_9: 事务

RabbitMQ中与事务机制相关的方法有3个

| 方法 | 解释 |
|:--:|:--:|
|channel.txSelect()  | 将当前的信道设置成事务模式|
|channel.txCommit()|提交事务|
|channel.txRollback()|回滚事务|

消息成功被发送到RabbitMQ的exchange上，事务才能提交成功，否则便可在捕获异常之后进行事务回滚，与此同时可以进行消息重发
因为事务会榨干RabbitMQ的性能，所以一般使用发布者确认代替事务

### chapter_10: 消息持久化

**消息做持久化，只需要将消息属性的delivery-mode设置为2即可**

RabbitMQ给我们封装了这个属性，即MessageProperties.PERSISTENT_TEXT_PLAIN，
详细使用可以参考github的代码

当我们想做消息的持久化时，最好同时设置队列和消息的持久化，因为只设置队列的持久化，重启之后消息会丢失。只设置队列的持久化，重启后队列消失，继而消息也丢失

### chapter_11: 死信队列

DLX，全称为Dead-Letter-Exchange，称之为死信交换器。当一个消息在队列中变成死信（dead message）之后，它能被重新发送到另一个交换器中，这个交换器就是DLX，绑定DLX的队列就称之为死信队列。
DLX也是一个正常的交换器，和一般的交换器没有区别，实际上就是设置某个队列的属性

**消息变成死信一般是由于以下几种情况**

1. 消息被拒绝（Basic.Reject/Basic.Nack）且不重新投递（requeue=false）
2. 消息过期
3. 队列达到最大长度

**死信交换器和备用交换器的区别**

备用交换器: 1.消息无法路由时转到备用交换器 2.备用交换器是在声明主交换器的时候定义的

死信交换器: 1.消息已经到达队列，但是被消费者拒绝等的消息会转到死信交换器。2.死信交换器是在声明队列的时候定义的

### chapter_12: 流量控制（服务质量保证）

qos即服务端限流，qos对于拉模式的消费方式无效

**使用qos只要进行如下2个步骤即可**
 
1. autoAck设置为false（autoAck=true的时候不生效）
 
2. 调用basicConsume方法前先调用basicQos方法，这个方法有3个参数
 
basicQos(int prefetchSize, int prefetchCount, boolean global)
 
|参数名| 含义 |
|:--:|:--:|
|prefetchSize|批量取的消息的总大小，0为不限制|
|prefetchCount|消费完prefetchCount条（prefetchCount条消息被ack）才再次推送|
|global|global为true表示对channel进行限制，否则对每个消费者进行限制，因为一个channel允许有多个消费者|
 
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
