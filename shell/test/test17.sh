#!/usr/bin/env bash

#模拟Java调用shell，并将输出定向到java。
#生产随机字符串
eval echo "${0},${1},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
current_dir=$(pwd)

echo "-----------start:${1}----------------"
echo "${1},current dir:${current_dir}"

for (( i=0; i<=1000000; i++ )) ; do
    seconds="$(date "+%s")"
    randCount=$(echo $RANDOM | cut -c1-4)
    echo "${1},${randCount},${seconds},${i}----------------------------"
    eval echo "$(cat /dev/urandom | head -n 10 | md5sum | head -c 32)"
    eval echo "$(cat /dev/urandom | head -n 10 | md5sum | head -c 32)"
    eval echo "$(cat /dev/urandom | head -n 10 | md5sum | head -c 32)"
    eval echo "$(cat /dev/urandom | head -n 10 | md5sum | head -c 32)"
    ls ad -mk
    #sleep 0.05
done

echo "-----------end:${1}----------------"

