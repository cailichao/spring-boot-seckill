## 两种情况：

1.带索引  2.不带索引

## 前提介绍：

方式：采用命令行的方式来模拟

1.mysq由于默认是开启自动提交事务，所以首先得查看自己当前的数据库是否开启了自动提交事务。

命令：select @@autocommit;

结果如下：


```
+--------------+
| @@autocommit |
+--------------+
|            0 |
+--------------+
```


如果是1，那么运行命令：set autocommit = 0;设置为不开启自动提交

2.当前的数据库表格式如下


```
tb_user | CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `operator` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8
```


显然除了主键，我没有加任何索引

 

## 实际例子：

1.没有索引

运行命令：begin;开启事务，然后运行命令：update tb_user set phone=11 where name="c1";修改，先别commit事务。

再开一个窗口，直接运行命令：update tb_user set phone=22 where name="c2";会发现命令卡住了，但是当前面一个事务通过commit提交了，命令就会正常运行结束，说明是被锁表了。

2.给name字段加索引

create index index_name on tb_user(name);

然后继续如1里面的操作，也就是一个开启事务，运行update tb_user set phone=11 where name="c1"；先不提交

然后另一个运行update tb_user set phone=22 where name="c2";发现命令不会卡住，说明没有锁表

但是如果另一个也是update tb_user set phone=22 where name="c1";更新同一行，说明是锁行了

3.总结

如果没有索引，所以update会锁表，如果加了索引，就会锁行


源自：https://www.cnblogs.com/wodebudong/articles/7976474.html