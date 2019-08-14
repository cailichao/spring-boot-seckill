通常情况下我们需要在两个Linux服务器之间拷贝文件，比如定时备份。


以博客为例，网站目录定时打包比如一周或者一个月，远程同步到备份服务器。


博客服务器输入一下命令，然后输入远程主机密码，即可进行同步拷贝：
```
scp -r /mnt/domains/blog.52itstyle.com_20181024.tar.gz  root@115.29.143.135:/home/backups/
```

如果想增量拷贝，我们可以使用rsync命令。


```
rsync -avz  /mnt/domains/blog.52itstyle.com  root@115.29.143.135:/home/backups/
```


如果出现：


```
RSA host key for [ip address] has changed and you have requested strict checking
```
可能是系统重装后，本地机和服务器内部ssh对不上导致错误，因此，只需要删除本地机ssh缓存信息，即可恢复。 
在本地机输入一下命令行：

```
ssh-keygen -R IP
```

