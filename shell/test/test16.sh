#!/usr/bin/env bash

#随机生产数字
#$RANDOM 的范围是 [0, 32767]
#cut -c1-4表示取4位
#echo $RANDOM | cut -c1-4
function rand(){
    min=$1
    max=$(($2-$min+1))
    num=$(($RANDOM+1000000000)) #增加一个10位的数再求余
    echo $(($num%$max+$min))
}

# 随机生产字符串
#使用date 生成随机字符串,head -c 10表示取前10位,最多取32位
date +%s%N | md5sum | head -c 10
#使用 /dev/urandom 生成随机字符串
cat /dev/urandom | head -n 10 | md5sum | head -c 10

# 模拟请求接口
start_seconds=$(date "+%s")
echo "${start_seconds}"
for (( i=0; i<=100000; i++ )) ; do
    seconds1="$(date "+%s")"
    diff=`expr ${seconds1} - ${start_seconds}`
    randCount=$(rand 300 500)

    code4=$(curl  "http://l-xxx.com:9090/sms/watcher?metrics=Test_Value&type=recordValue&count=${randCount}" 2>/dev/null)
    echo "${code4},${randCount}"
done
