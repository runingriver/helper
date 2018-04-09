#!/usr/bin/env bash

FILE="/home/zongzhehu/Downloads/xab"

#获取文件中,以空格作为分隔符,的每一列,赋值给特定变量.
while read line
do
    echo ${line}
    l1=$(echo ${line} | cut -d " " -f 1)
    echo ${l1}

    l2=$(echo ${line} | cut -d " " -f 2)
    echo ${l2}

    l3=$(echo ${line} | cut -d " " -f 3)
    echo ${l3}
    exit;
done < ${FILE}