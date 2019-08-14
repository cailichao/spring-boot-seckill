linux下用top命令查看cpu利用率超过100%，这里显示的所有的cpu加起来的使用率，说明你的CPU是多核，你运行top后按大键盘1看看，可以显示每个cpu的使用率，top里显示的是把所有使用率加起来。

按下1后可以看到我的机器的CPU是双核的。%Cpu0，%Cpu1，%Cpu2......

![输入图片说明](https://images.gitee.com/uploads/images/2018/1009/090959_66c1e25f_87650.png "9Y70@H`2}N7@LB6SOXKGF)E.png")

这里我们也可以查看一下CPU信息：在命令行里输入：cat /proc/cpuinfo


这里可以看到cpu cores : 11

