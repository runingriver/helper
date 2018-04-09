#!/usr/bin/env bash

#读取目录下的文件每一行，并将这行数据发送给接口
eval echo "${0},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
current_dir=$(pwd)
file_path="${current_dir}/${1}"

count=0
cat ${file_path} |  while read line
do
code=$(curl --data-urlencode "data=${line}" "http://xxx.com:9191/shell/hive" 2>/dev/null)
count=`expr ${count} + 1`
echo "${count}:${code}"
done



