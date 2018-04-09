#!/usr/bin/env bash

# 将Windows换行符('\n\r')缓存Linux下的('\n').
# 语法:sed 's/要被取代的字串/新的字串/g'
#sed 's/\r//g'

#获取当前目录
currentDir=$(cd $(dirname $0);pwd)
DIR="$( cd "$( dirname "$0" )" && pwd )"
echo "currentDir:${currentDir}, 进程Pid: $$"
echo "DIR: ${DIR}"

#时间处理--------------------------------------------------------------
#获取时间戳
TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")
echo "TIMESTAMP:${TIMESTAMP}"
line="$(date -d "1 days ago" +'%Y%m%d')"
echo "line:${line}"

#自定义获取时间,并定义格式
SET_DATE="20170303"
line2="$(date -d "${SET_DATE} 1 days ago" +'%Y%m%d')"
echo "line2:${line2}"
#end 时间处理-----------------------------------------------------------

#循环
for (( i=0;i<10;i=i+1 )) do
echo "$i"
done

#从文件中读取一行
while read mobile
do
echo ${mobile}
done  < tip.txt

nohup echo "hello"
echo "$! , $?"
