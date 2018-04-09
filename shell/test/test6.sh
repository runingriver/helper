#!/usr/bin/env bash

echo "进程Pid: $$"
#将文本文件里面(mobile,count)字段插入到mysql中
insertIntoMysql(){
    #获取参数
    path=${1}
    col=${2}
    TIMESTAMP=$(date +%Y%m%d%H%M%S)
    echo "path:${path},column:${col},time:${TIMESTAMP}"
    #遍历文件每一行
    cat ${path} | while read line
    do
        #获取每一行中的每一列
        mobile=$(echo -e "${line}" | cut -f 1)
        count=$(echo -e "${line}" | cut -f 2)
        #写入myusql
        cmd="INSERT INTO sms.sms_financial(mobile,${col}) VALUES ('${mobile}',${count}) ON DUPLICATE KEY UPDATE ${col}=${count};"
        eval $(mysql -uroot -p111 --default-character-set=utf8 -e "${cmd}")
        echo "mobile:${mobile},count:${count}"
    done

    TIMESTAMP=$(date +%Y%m%d%H%M%S)
    echo "end time:${TIMESTAMP}"
}

#保存每个字段(mobile,count)的文件目录
path="/home/q/part1"
eval cd ${path}

line=$(find ${path} -type f)
for s in ${line[@]}
do
    #截取文件名，即mysql table中对应的列名！
    col=$(echo ${s} |cut -d "/" -f5)
    insertIntoMysql ${s} ${col}
done
exit;