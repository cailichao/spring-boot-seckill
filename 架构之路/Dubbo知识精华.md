####  1、默认使用的是什么通信框架，还有别的选择吗?

默认也推荐使用netty框架，还有mina。

####  2、服务调用是阻塞的吗？

默认是阻塞的，可以异步调用，没有返回值的可以这么做。

####  3、一般使用什么注册中心？还有别的选择吗？

推荐使用zookeeper注册中心，还有redis等不推荐。

![输入图片说明](https://images.gitee.com/uploads/images/2018/0831/153501_9a059fcf_87650.png "SPI.png")

####  4、默认使用什么序列化框架，你知道的还有哪些？

默认使用Hessian序列化，还有Duddo、FastJson、Java自带序列化。

####  5、服务提供者能实现失效踢出是什么原理？

服务失效踢出基于zookeeper的临时节点原理。

####  6、服务上线怎么不影响旧版本？

采用多版本开发，不影响旧版本。

####  7、如何解决服务调用链过长的问题？

可以结合zipkin实现分布式服务追踪。

#### 8、说说核心的配置有哪些？

核心配置有


```
dubbo:service/

dubbo:reference/

dubbo:protocol/

dubbo:registry/

dubbo:application/

dubbo:provider/

dubbo:consumer/

dubbo:method/
```

####  9、dubbo推荐用什么协议？

默认使用dubbo协议。

####  10、同一个服务多个注册的情况下可以直连某一个服务吗？

可以直连，修改配置即可，也可以通过telnet直接某个服务。


####  11、Dubbo集群容错怎么做？

读操作建议使用Failover失败自动切换，默认重试两次其他服务器。写操作建议使用Failfast快速失败，发一次调用失败就立即报错。


####  12、dubbo和dubbox之间的区别？

dubbox是当当网基于dubbo上做了一些扩展，如加了服务可restful调用，更新了开源组件等。

####  13、你还了解别的分布式框架吗？

别的还有spring的spring cloud，facebook的thrift，twitter的finagle等。

#### 14、dubbo中zookeeper做注册中心，如果注册中心集群都挂掉，那发布者和订阅者还能通信吗?

可以的，zookeeper的信息会缓存到本地作为一个缓存文件，并且转换成properties对象方便使用。

```
<!-- 使用zookeeper注册中心暴露服务地址 subscribe 默认：true 是否向此注册中心订阅服务，如果设为false，将只注册，不订阅 check 默认：true 注册中心不存在时，是否报错    -->
<dubbo:registry protocol="zookeeper" address="${dubbo.registry.address}" file="${dubbo.registry.file}" check="false"/>

```

```
dubbo.registry.address=172.16.1.130:2181,172.16.1.133:2181,172.16.1.120:2181
dubbo.registry.file=/root/.dubbo/pay/dubbo.cache
```

#### 15、项目中有使用过多线程吗?有的话讲讲你在哪里用到了多线程?


```
<!-- 
  生产者配置 生产者  远程默认调用3次 参数 retries="2" async="true" 异步返回结果 默认是同步 timeout="10000" 毫秒
  用dubbo协议在20882端口暴露服务  固定线程池 10 启动时建立线程，不关闭，一直持有  负载均衡策略 轮询
	 -->
<dubbo:provider  timeout="10000"  threads="10" threadpool="fixed" loadbalance="roundrobin"/>
```

#### 16、Zookeeper的Java客户端你使用过哪些?

![输入图片说明](https://images.gitee.com/uploads/images/2018/0831/154522_65ef08bd_87650.png "Zk.png")