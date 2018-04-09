#!/usr/bin/env bash

#普通循环
COUNTER=0
while [ $COUNTER -lt 5 ]
do
COUNTER=`expr $COUNTER + 1`
echo $COUNTER
done

#循环获取输入
echo 'type <CTRL-D> to terminate'
echo -n 'enter your most liked film: '
while read FILM
do
echo "Yeah! great film the $FILM"
done

# 从文件中读取数据执行
while read mobile
do
    echo $mobile
    sleep 0.005
done  < ${mobile_file}

#使用read命令读取一行数据
IFS=':'    ## 改变字段分隔符
cat ${mobile_file} | while read myline
do
    echo "LINE:"${myline}
done

# 从文件读入：定义一行分隔符,将每列值赋值给对应变量
#从/etc/passwd文件中读取用户名并输出
oldIFS=$IFS #IFS是文件内部分隔符
IFS=":"     #设置分隔符为:
while  read username var1 var2 #var变量不可少
do
    echo "username:${username}, var1=${var1}, var2=${var2}"
done < /etc/passwd
IFS=${oldIFS}
#注：while [命令]

#while 命令
#do
#    循环体
#done < 文件名
#相当于将文件内容逐行传递给while后面的命令(类似管道)，然后再执行循环体。


#读出到文件
#每隔10分钟，ping一下局域网内主机192.168.1.101，
#并把结果记录到ping.txt文件中
while date
do
    ping -c5 192.168.1.101 >/dev/null 2>&1
    if [ $? = 0 ];then
        echo OK
    else
        echo FAIL
    fi
    sleep 600 #600秒是10分钟
done > ping.txt

#while 命令
#do
#    循环体
#done > 文件名
#这个结构会将命令的输出，以及循环体中的标准输出都重定向到指定的文件中。


#菜单demo
while :
do
    echo   #输出空行
    echo "========================="
    echo "      1：输出成绩单"
    echo "      2：输出课程表"
    echo "      3：输出空闲教室"
    echo "      q：退出菜单"
    echo "========================="
    read -p"请输入：" input
    case $input in
        1)echo "稍等，正在为您输出成绩单";;
        2)echo "稍等，正在为您输出课程表";;
        3)echo "稍等，正在为您输出空闲教";;
        q|Q) exit
    esac
done