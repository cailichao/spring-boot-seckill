#### 列出PID和Java主类名

```
jps

2017 Bootstrap
2576 Jps
```

#### 列出pid和java完整主类名

```
jps -l

2017 org.apache.catalina.startup.Bootstrap
2612 sun.tools.jps.Jps
```

#### 列出pid、主类全称和应用程序参数

```
jps -lm

2017 org.apache.catalina.startup.Bootstrap start
2588 sun.tools.jps.Jps -lm
```

#### 列出pid和JVM参数

```

jps -v

2017 Bootstrap -Djava.util.logging.config.file=/usr/local/tomcat-web/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Dfile.encoding=UTF-8 -Xms256m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m -verbose:gc -Xloggc:/usr/local/tomcat-web/logs/gc.log-2014-02-07 -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xnoclassgc -Djava.endorsed.dirs=/usr/local/tomcat-web/endorsed -Dcatalina.base=/usr/local/tomcat-web -Dcatalina.home=/usr/local/tomcat-web -Djava.io.tmpdir=/usr/local/tomcat-web/temp
2624 Jps -Dapplication.home=/usr/lib/jvm/jdk1.6.0_43 -Xms8m
```

#### 和【ps -ef | grep java】类似的输出

```
jps -lvm

2017 org.apache.catalina.startup.Bootstrap start -Djava.util.logging.config.file=/usr/local/tomcat-web/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Dfile.encoding=UTF-8 -Xms256m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m -verbose:gc -Xloggc:/usr/local/tomcat-web/logs/gc.log-2014-02-07 -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xnoclassgc -Djava.endorsed.dirs=/usr/local/tomcat-web/endorsed -Dcatalina.base=/usr/local/tomcat-web -Dcatalina.home=/usr/local/tomcat-web -Djava.io.tmpdir=/usr/local/tomcat-web/temp
2645 sun.tools.jps.Jps -lvm -Dapplication.home=/usr/lib/jvm/jdk1.6.0_43 -Xms8m
```

 