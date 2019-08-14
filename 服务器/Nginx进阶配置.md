### 静态文件服务器

在Java开发过程以及生产环境中，最常用的web应用服务器当属Tomcat，尽管这只猫也能够处理一些静态请求，例如图片、html、样式文件等，但是效率并不是那么尽人意。所以在生产环境中，我们一般使用Nginx代理服务器来处理静态文件，来提升网站性能。

```
server {
        listen  80;
        server_name  file.52itstyle.com;
        charset utf-8;
        #root 指令用来指定文件在服务器上的基路径
        root /data/statics;
        #location指令用来映射请求到本地文件系统
        location / {
           autoindex on; # 索引
           autoindex_exact_size on; # 显示文件大小
           autoindex_localtime on; # 显示文件时间
        }
   }
```

### 负载均衡

Nginx 提供轮询（round robin）、IP 哈希（client IP）和加权轮询 3 种方式，默认情况下，Nginx 采用的是轮询。

#### 轮询（默认）
每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器down掉，能自动剔除。 
`````
upstream backserver { 
server 192.168.1.14; 
server 192.168.1.15; 
} 
`````
#### 加权轮询
指定轮询几率，weight和访问比率成正比，用于后端服务器性能不均的情况。 
```
upstream backserver { 
server 192.168.1.14 weight=1; 
server 192.168.1.15 weight=2; 
} 
```
####  ip_hash
每个请求按访问ip的hash结果分配，这样每个访客固定访问一个后端服务器，可以解决session的问题。 
```
upstream backserver { 
ip_hash; 
server 192.168.0.14; 
server 192.168.0.15; 
} 
```

#### 重试策略
可以为每个 backserver 指定最大的重试次数，和重试时间间隔,所使用的关键字是 max_fails 和 fail_timeout。
```
upstream backserver { 
server 192.168.1.14  weight=1  max_fails=2 fail_timeout=30s; 
server 192.168.1.15  weight=2  max_fails=2 fail_timeout=30s;
} 
```
失败重试次数为3，且超时时间为30秒。

#### 热机策略
```
upstream backserver { 
server 192.168.1.14  weight=1  max_fails=2 fail_timeout=30s; 
server 192.168.1.15  weight=2  max_fails=2 fail_timeout=30s;

server 192.168.1.16 backup;
}
```
当所有的非备机（non-backup）都宕机（down）或者繁忙（busy）的时候，就会使用由 backup 标注的备机。必须要注意的是，backup 不能和 ip_hash 关键字一起使用。

### WebSocket配置实例

[Nginx学习之反向代理WebSocket配置实例 ](https://blog.52itstyle.com/archives/736/)

