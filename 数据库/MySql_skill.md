## MySql 小技巧

#### 1）修改默认时区

select now(); 查看 MySql 系统时间。和当前时间做对比

set global time_zone = '+8:00';设置时区更改为东八区

flush privileges; 刷新权限

#### 2）批量删除以字段开头的表

```
# 先查询
SELECT GROUP_CONCAT(table_name) FROM information_schema.tables WHERE table_schema='itstyle' AND table_name LIKE 'add%'
# 拷贝出来
DROP TABLE  add_student,add_teacher
```

#### 3）查看所有连接进程

```
show full processlist 
```

看一下所有连接进程，注意查看进程等待时间以及所处状态 是否locked

如果进程过多，就把进程打印下来，然后查看

```
mysql -e 'show full processlist;' > list.txt
```

#### 4）创建一个只读权限的用户

```
# 创建查询用户、允许外网访问
CREATE USER 'select'@'%' IDENTIFIED BY '123456';
# 给新用户赋予查询权限
GRANT SELECT ON * . * TO 'select'@'%';
# 刷新权限
FLUSH PRIVILEGES;
```
