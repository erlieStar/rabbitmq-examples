![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007142508554.jpg?)
## 安装RabbitMQ
### Docker安装（建议）
可能有小伙伴没用过Docker，这里就简单介绍一下Docker的安装

**centos7安装**

```shell
# 安装
yum install docker
# 如果想卸载，可以先查找安装的docker包，然后remove即可
yum list installed | grep docker
yum remove -y（-y不询问）包名
```
**启动**

```shell
# 启动docker
service docker start
# 关闭docker
service docker stop
# 设置开机启动
sudo chkconfig docker on（开机启动）
```
测试

```shell
docker info
```
正确输出信息则表明docker启动成功

最好配置一个阿里云的镜像，速度很快，官方的地址，官方在中国市场的镜像地址都很慢

登陆阿里云官网，搜索容器镜像服务，复制自己的加速器地址到/etc/docker/daemon.json文件即可

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007111828998.png?)

```shell
vi /etc/docker/daemon.json
```
写入如下内容
```json
{
	"registry-mirrors":["从阿里云复制的地址"]
}
```

获取RabbitMQ和管控台并启动

```shell
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

同时启动了RabbitMQ服务端和管控台，此时访问

```shell
http://ip:15672
```
输入用户名guest和密码guest即可进入管控台页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100611122292.jpeg?)
### Linux安装（3.6版本）

rabbitmq和erlang的版本关系，2者版本必须要一致，不然各种问题。对照关系链接如下
https://www.rabbitmq.com/which-erlang.html

我装的rabbitmq的版本是3.6.10
erlang的版本是19.3
**下载erlang**
```shell
wget http://erlang.org/download/otp_src_19.3.tar.gz
tar -xvf otp_src_19.3.tar.gz
cd otp_src_19.3
./configure --prefix=/opt/soft/erlang
make
make install
```
配置环境变量

```shell
vim /etc/profile
export ERLANG_HOME=/opt/soft/erlang
export PATH=$PATH:$ERLANG_HOME/bin
source /etc/profile
```
验证是否正常安装，正常输出版本则正确

```javascript
[root@VM_0_14_centos soft]# erl
Erlang/OTP 19 [erts-8.3] [source] [64-bit] [smp:2:2] [async-threads:10] [hipe] [kernel-poll:false]

Eshell V8.3  (abort with ^G)
1>
```
**下载rabbitmq**

```shell
wget https://www.rabbitmq.com/releases/rabbitmq-server/v3.6.10/rabbitmq-server-generic-unix-3.6.10.tar.xz
tar -Jxf rabbitmq-server-generic-unix-3.6.10.tar.xz
mv rabbitmq_server-3.6.10 rabbitmq
```
配置环境变量

```shell
vim /etc/profile
export PATH=$PATH:/opt/soft/rabbitmq/sbin
export RABBITMQ_HOME=/opt/soft/rabbitmq
source /etc/profile
```

修改配置文件

```shell
vim /usr/lib/rabbitmq/lib/rabbitmq_server-3.5.0/ebin/rabbit.app
// ./loopback_users，将loopback_users属性设置为如下内容
{loopback_users,["guest"]},
```

操作

```shell
# 后台启动
rabbitmq-server -detached
# 查看集群状态（单机也行）
rabbitmqctl cluster_status
# 管理插件（启动管控台页面）
rabbitmq-plugins enable rabbitmq_management
# 访问地址
ip:15672 
# 关机
rabbitmqctl stop_app
```
## RabbitMQ图形界面的使用
在网上看到有一篇文章写的很好，这里分享一下
原文地址：https://www.cnblogs.com/biehongli/p/11874086.html

1、RabbitMQ的管控台确实是一个好东西，但是如果是新手，比如刚接触RabbitMQ的时候，看到RabbitMQ的管控台也是一脸懵逼的说，慢慢接触多了，才了解一些使用。

1.1、RabbitMQ的管控台中概览的Totals。如果有消息进行消费的话，如果我们创建队列的话，这里面显示消费进度和情况，实施进行显示，可以看到一个折线图的表现形式。RabbitMQ的管控台中概览的Global counts。Connections代表了有多少链接，Channels代表了有多少网络通信信道，Exchanges代表了有多少交换机，Queues代表了有多少队列，Consumers代表了有多少消费者。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115659691.png?)
当你的生产者和消费者启动以后，可以看到连接个数，网络通信信道个数，交换机个数，队列个数，消费者个数。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115726749.png?)

1.2、RabbitMQ的管控台中概览的Nodes表示当前节点的情况状态，File descriptors是文件描述，Socket descriptors是通信情况，Erlang processes代表了Erlang的进程数，Memory代表了整个服务的内存使用情况，Disk space代表了磁盘的使用情况，Rates mode，Info代表了存储状态。path对应了RabbitMQ的一些配置路径，Config file代表了存储路径，Database directory代表了数据的存储路径，Log file代表了日志文件的存储路径。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100711574360.png?)

1.3、RabbitMQ的管控台中概览的Ports and contexts，是RabbitMQ提供的端口号都代表了什么含义，amqp默认是5672，clustering集群默认是25672。Web contexts代表了RabbitMQ管控台的端口号是15672。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115758932.png?)

1.4、RabbitMQ的管控台中概览的Import / export definitions，可以导入和导出文件的定义，可以方便的导入和到处Rabbitmq的一些配置文件。比如可以导出交换器，虚拟主机，队列等等。在升级的时候非常有用的。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115814523.png?)


2、RabbitMQ的管控台中Connections链接，应用服务和RabbitMQ的链接。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115833560.png?)
如果有连接的时候，效果如下所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115849799.png?)

点击连接名称Name的时候，可以看到该连接的详细信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115915505.png?)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007115942699.png?)
3、RabbitMQ的管控台中Channels网络通信信道，应用服务和RabbitMQ的进行的操作都需要建立连接，然后使用Channel进行实际的操作。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120009371.png?)
如果有连接的时候，创建了网络通信信道，效果如下所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120029858.png?)

点击Channel网络通信信道，可以查看详细信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120047299.png?)

4、RabbitMQ的管控台中Exchanges交换机，生产者直接将消息投递到交换机，默认提供一些交换机。

注意：如果不指定交换机，默认使用(AMQP default)这个交换机，类型direct直连的方式，发布订阅模式。AMQP default路由规则是根据路由键Routing key，去队列列表里面寻找相同名称的队列，如果有，将生产者生产的消息投递到该队列里面。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100712014295.png?)
durable:true代表了持久化存储，即使RabbitMQ服务停掉了，重新启动RabbitMQ服务的时候，这些持久化的交换机也不会被清除的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120307987.png?)

可以添加一个交换机，方式如下所示，可以选择自己的配置，建议使用代码哦。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120323241.png?)
可以查看新建的交换机的详细信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120349371.png?)
可以生产消息，查看队列是否接收到了生产的这条消息。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120504568.png?)

5、RabbitMQ的管控台中Queues队列。

Features特性，durable:true代表了持久化存储，即使RabbitMQ服务停掉了，重新启动RabbitMQ服务的时候，这些持久化的交换机也不会被清除的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120523326.png?)

在新增队列的时候可以选择一些参数，进行设置的。如下所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120541200.png?)

Total=Ready+Unacked，消息总数等于生产的待消费的消息Ready加上未被ack消息确认的消息。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100712055474.png?)

创建的队列点进去以后，可以查看队列的详情。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100712060944.png?)
可以查看交换机通过路由键绑定的队列，也可以新增一个交换机，通过路由键，和队列进行绑定。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100712062999.png?)
可以在这个队列里面，生产消息，进行观察测试使用。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120645894.png?)

获取到生产者生产的消息，可以进行测试，可以指定获取消息的条数。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120658141.png?)

删除队列，或者异常该队列里面的消息，我的暂时不能操作。或者观察

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120712343.png?)

清空队列里面的消息，可以使用Purge。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120724183.png?)
6、RabbitMQ的管控台中Admin用户。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120743649.png?)
可以查看虚拟主机的信息，如下所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120757505.png?)
点击虚拟主机的名称，可以看到详细信息，如下所示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007120810623.png?)
我用一个思维导图总结一下这个管控台的常用功能

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201007134544883.png?)

