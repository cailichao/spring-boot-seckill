linux SSH默认端口是22，不修改的话存在一定的风险，要么是被人恶意扫描，要么会被人破解或者攻击，所以我们需要修改默认的SSH端口。
```
vi /etc/ssh/sshd_config 
```
默认端口是22，并且已经被注释掉了，打开注释修改为其他未占用端口即可。

开启防火墙端口并重复服务即可。
```
systemctl restart sshd.service
```