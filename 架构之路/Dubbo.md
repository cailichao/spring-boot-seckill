### 【什么是 dubbo】

Dubbo 是阿里巴巴开发用来用来治理服务中间件，资源调度和治理中心的管理工具。

### 【ZooKeeper 节点类型】

ZooKeeper 节点是有生命周期的，这取决于节点的类型，在 ZooKeeper 中，节点类型可以分为：

- 持久节点（PERSISTENT ）
是指在节点创建后，就一直存在，直到有删除操作来主动清除这个节点
- 临时节点（EPHEMERAL）
临时节点的生命周期和客户端会话绑定。如果客户端会话失效，那么这个节点就会自动被清除掉。在临时节点下面不能创建子节点。
- 持久顺序节点（PERSISTENT_SEQUENTIAL）
在持久节点的基础上，在ZK中，每个父节点会为他的第一级子节点维护一份时序，会记录每个子节点创建的先后顺序
- 临时顺序节点（EPHEMERAL_SEQUENTIAL）
可以用来实现分布式锁【[从构建分布式秒杀系统聊聊分布式锁](https://blog.52itstyle.com/archives/3202/ "从构建分布式秒杀系统聊聊分布式锁")】


### 【dubbo节点角色说明】
- Provider: 暴露服务的服务提供方（service 服务层）。
- Consumer: 调用远程服务的服务消费方(web 表现层)。
- Registry: 服务注册与发现的注册中心（zookeeper）。
- Monitor: 统计服务的调用次调和调用时间的监控中心。
- Container: 服务运行容器(tomcat 容器，spring 容器)。

### 【dubbo的注册原理】zookeeper流程
- 服务提供者启动时向/dubbo/com.foo.BarService/providers 目录下写入自己的 URL 地址。
- 服务消费者启动时订阅/dubbo/com.foo.BarService/providers 目录下的提供者 URL 地址。
- 并向/dubbo/com.foo.BarService/consumers 目录下写入自己的 URL 地址。
- 监控中心启动时订阅/dubbo/com.foo.BarService 目录下的所有提供者和消费者 URL 地址。

### 【注册中心包括】
- Multicast 注册中心不需要启动任何中心节点，只要广播地址一样，就可以互相发现。
- Zookeeper 是 Apacahe Hadoop 的子项目，是一个树型的目录服务，支持变更推送，适合作为 Dubbo 服务的注册中心，工业强度较高，可用于生产环境，并推荐使用
- 基于 Redis 实现的注册中心 
- Simple 注册中心本身就是一个普通的 Dubbo 服务，可以减少第三方依赖，使整体通讯方式一致。

### 【dubbo,zookeeper流程,从生产者到消费者】
- 生产者和消费者都要进行dubbo的配置 ,都需要注册zookeeper主机地址,
- 生产者要配置dubbo使用的协议(默认dubbo)和端口号用来暴露服务,
- 生产者定义接口和实现类,并在配置文件中进行注册服务,
- 生产者启动时会自动把注册的接口的信息转化为一个url,
- 并通过hessian二进制序列化存储到zookeeper的节点中
- 消费者在配置文件中引入要使用的服务接口,
- 消费者启动时会从zookeeper中获取与引用的接口匹配的url,
- 并把自己的信息留在zookeeper中
- 服务者和消费者在zookeeper中的信息都会被监控中心monitor获取到,
- 可以通过monitor服务对zookeeper中的内容进行管理

### 【服务的配置】
```xml
<!--【服务者】 给服务起一个名称,唯一标识,用来监控服务器调用关系,调用次数 -->
<dubbo:application name="provider" />
<!-- 使用dubbo官方推荐注册中心模式注册对象 -->
<dubbo:registry address="zookeeper://192.168.1.180:2181" />
<!-- 用dubbo协议在20880端口暴露服务 -->
<dubbo:protocol name="dubbo" port="20880" />
<!-- 发布服务:itemService 注册对象,通过接口来注册对象 -->
<!-- 和本地bean一样实现服务 -->
<bean id="xxx.xxx.xxx.XxxImpl" class="xxx.xxx.xxx.XxxImpl" />
<dubbo:service interface="xxx.xxx.xxx.Ixxx" ref="xxxImpl" />

<!-- 【消费者】给服务起一个名称,唯一标识,用来监控服务器调用关系,调用次数 -->
<!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
<dubbo:application name="consumer" />
<!-- 使用multicast广播注册中心暴露发现服务地址 -->
<dubbo:registry address="zookeeper://192.168.17.129:2181" />
<!-- 生成远程服务代理，可以和本地bean一样使用itemService -->
<dubbo:reference id="xxx" interface="xxx.xxx.xxx.Ixxx" timeout="1000000" retries="2"/>
```

### 文档

http://dubbo.apache.org/zh-cn/docs/dev/design.html