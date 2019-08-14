## 运行模式
Tomcat Connector三种运行模式（BIO, NIO, APR）的比较和优化。

```
org.apache.coyote.http11.Http11Protocol：BIO
org.apache.coyote.http11.Http11NioProtocol：NIO
org.apache.coyote.http11.Http11Nio2Protocol：NIO2
org.apache.coyote.http11.Http11AprProtocol：APR
```
### BIO

一个线程处理一个请求。缺点：并发量高时，线程数较多，浪费资源。Tomcat7或以下，在Linux系统中默认使用这种方式。

### NIO

利用Java的异步IO处理，可以通过少量的线程处理大量的请求。Tomcat8在Linux系统中默认使用这种方式。Tomcat7必须修改Connector配置来启动：
```xml
<Connector port="8080" 
           protocol="org.apache.coyote.http11.Http11NioProtocol"
           connectionTimeout="20000"
		   redirectPort="8443"/>
```
Tomcat8以后NIO2模式：
```xml
<Connector  port="8080"
            protocol="org.apache.coyote.http11.Http11Nio2Protocol"
            connectionTimeout="20000"
		    redirectPort="8443"/>
```
### APR

即Apache Portable Runtime，从操作系统层面解决io阻塞问题。Tomcat7或Tomcat8在Win7或以上的系统中启动默认使用这种方式。Linux如果安装了apr和native，Tomcat直接启动就支持apr。

## 连接池

默认值：
```xml
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
```

修改为：
```xml
<Executor 
    name="tomcatThreadPool" 
    namePrefix="catalina-exec-"
    maxThreads="500" 
    minSpareThreads="100" 
    prestartminSpareThreads = "true"
    maxQueueSize = "100"
/>
```

参数解释：
- maxThreads，最大并发数，默认设置 200，一般建议在 500 ~ 800，根据硬件设施和业务来判断
- minSpareThreads，Tomcat 初始化时创建的线程数，默认设置 25
- prestartminSpareThreads，在 Tomcat 初始化的时候就初始化 minSpareThreads 的参数值，如果不等于 true，minSpareThreads 的值就没啥效果了
- maxQueueSize，最大的等待队列数，超过则拒绝请求

默认的链接参数配置：
```xml
<Connector 
    port="8080" 
    protocol="HTTP/1.1" 
    connectionTimeout="20000" 
    redirectPort="8443" 
/>
```
修改为：
```xml
<Connector  executor="tomcatThreadPool"
            port="8080"
            protocol="org.apache.coyote.http11.Http11Nio2Protocol"
            connectionTimeout="20000"
		    redirectPort="8443"/>
```

参数解释：
- protocol，Tomcat 8 设置 nio2 更好：org.apache.coyote.http11.Http11Nio2Protocol
- protocol，Tomcat 6、7 设置 nio 更好：org.apache.coyote.http11.Http11NioProtocol
- enableLookups，禁用DNS查询
- acceptCount，指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理，默认设置 100
- maxPostSize，以 FORM URL 参数方式的 POST 提交方式，限制提交最大的大小，默认是 2097152(2兆)，它使用的单位是字节。10485760 为 10M。如果要禁用限制，则可以设置为 -1
- acceptorThreadCount，用于接收连接的线程的数量，默认值是1。一般这个指需要改动的时候是因为该服务器是一个多核CPU，如果是多核 CPU 一般配置为 2

## 端口配置

Tomcat服务器需配置三个端口才能启动，安装时默认启用了这三个端口，当要运行多个tomcat服务时需要修改这三个端口。

```xml
<!-- 端口-1即可，标识随机 -->
<Server port="-1" shutdown="SHUTDOWN">
```

```xml
<!-- 访问端口，必须配置 -->
<Connector  port="8080"
            protocol="org.apache.coyote.http11.Http11Nio2Protocol"
            connectionTimeout="20000"
		    redirectPort="8443"/>
```
```xml
<!-- 配置Apache使用，如果使用Nginx代理注释掉即可 -->
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
```

## JVM 优化

Java 的内存模型分为：

-  Young，年轻代（易被 GC）。Young 区被划分为三部分，Eden 区和两个大小严格相同的 Survivor 区，其中 Survivor 区间中，某一时刻只有其中一个是被使用的，另外一个留做垃圾收集时复制对象用，在 Young 区间变满的时候，minor GC 就会将存活的对象移到空闲的Survivor 区间中，根据 JVM 的策略，在经过几次垃圾收集后，任然存活于 Survivor 的对象将被移动到 Tenured 区间。

-  Tenured，终身代。Tenured 区主要保存生命周期长的对象，一般是一些老的对象，当一些对象在 Young 复制转移一定的次数以后，对象就会被转移到 Tenured 区，一般如果系统中用了 application 级别的缓存，缓存中的对象往往会被转移到这一区间。

-  Perm，永久代。主要保存 class,method,filed 对象，这部门的空间一般不会溢出，除非一次性加载了很多的类，不过在涉及到热部署的应用服务器的时候，有时候会遇到 java.lang.OutOfMemoryError : PermGen space 的错误，造成这个错误的很大原因就有可能是每次都重新部署，但是重新部署后，类的 class 没有被卸载掉，这样就造成了大量的 class 对象保存在了 perm 中，这种情况下，一般重新启动应用服务器可以解决问题。

Linux 修改 /tomcat/bin/catalina.sh 文件，把下面信息添加到文件第一行。

机子内存如果是 8G，一般 PermSize 配置是主要保证系统能稳定起来就行：
```
JAVA_OPTS="-Dfile.encoding=UTF-8 -server -Xms6144m -Xmx6144m -XX:NewSize=1024m -XX:MaxNewSize=2048m -XX:PermSize=512m -XX:MaxPermSize=512m -XX:MaxTenuringThreshold=10 -XX:NewRatio=2 -XX:+DisableExplicitGC"
```

参数说明：
```
-Dfile.encoding：默认文件编码
-server：表示这是应用于服务器的配置，JVM 内部会有特殊处理的
-Xmx1024m：设置JVM最大可用内存为1024MB
-Xms1024m：设置JVM最小内存为1024m。此值可以设置与-Xmx相同，以避免每次垃圾回收完成后JVM重新分配内存。
-XX:NewSize：设置年轻代大小
-XX:MaxNewSize：设置最大的年轻代大小
-XX:PermSize：设置永久代大小
-XX:MaxPermSize：设置最大永久代大小
-XX:NewRatio=4：设置年轻代（包括 Eden 和两个 Survivor 区）与终身代的比值（除去永久代）。设置为 4，则年轻代与终身代所占比值为 1：4，年轻代占整个堆栈的 1/5
-XX:MaxTenuringThreshold=10：设置垃圾最大年龄，默认为：15。如果设置为 0 的话，则年轻代对象不经过 Survivor 区，直接进入年老代。对于年老代比较多的应用，可以提高效率。如果将此值设置为一个较大值，则年轻代对象会在 Survivor 区进行多次复制，这样可以增加对象再年轻代的存活时间，增加在年轻代即被回收的概论。
-XX:+DisableExplicitGC：这个将会忽略手动调用 GC 的代码使得 System.gc() 的调用就会变成一个空调用，完全不会触发任何 GC
```
