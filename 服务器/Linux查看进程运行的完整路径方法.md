通过ps及top命令查看进程信息时，只能查到相对路径，查不到的进程的详细信息，如绝对路径等。这时，我们需要通过以下的方法来查看进程的详细信息：

/proc
Linux在启动一个进程时，系统会在/proc下创建一个以PID命名的文件夹，在该文件夹下会有我们的进程的信息， 
其中包括一个名为exe的文件即记录了绝对路径，通过ll或ls –l命令即可查看。 

```
ll /proc/PID
```

比如，我们查看mongo使用以下命令：

```
[root@rmpapp local]# ps -ef|grep mongo
root      9466  6380  0 13:41 pts/1    00:00:00 grep --color=auto mongo
root     16053     1  0 8月14 ?       06:45:52 ./mongod --config mongodb.conf
```
然后：

```
ll /proc/16053
```

