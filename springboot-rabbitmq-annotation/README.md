## 说明

这个模块用了注解来指定绑定关系，消息确认用了自动ack

## ack

rabbitmq消息确认有三种方式

| 确认方式 | 解释 |
|--|--|
| AcknowledgeMode.NONE | 没有ack |
| AcknowledgeMode.MANUAL | 手动确认 |
| AcknowledgeMode.AUTO | 消费端收到消息自动ack |

一般在应用中我们需要手动ack





