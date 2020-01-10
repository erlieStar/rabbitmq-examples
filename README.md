# RabbitMQ入门教程

## rabbitmq-api（rabbitmq api的适用）

### chapter_1: 快速开始，手写一个RabbitMQ的生产者和消费者

### chapter_2: 演示了各种exchange的使用

|交换机属性|类型|
|:--:|:--:|
|Name|交换器名称|
|Type|交换器类型，有如下四种，direct，topic，fanout，headers|
|Durability|是否需要持久化，true为持久化。持久化可以将交换器存盘，在服务器重启的时候不会丢失相关信息|
|Auto Delete|与这个Exchange绑定的Queue或Exchange都与此解绑时，会删除本交换器|
|Internal|设置是否内置，true为内置。如果是内置交换器，客户端无法发送消息到这个交换器中，只能通过交换器路由到交换器这种方式|
|argument|其他一些结构化参数|

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