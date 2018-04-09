#!/usr/bin/env bash

#将文本文件里面（mobile，count）字段插入到mysql中
insertIntoMysql(){
    path=${1}
    col=${2}
    echo "path:${path},column:${col}"
    cat ${path} | while read line
    do
        mobile=$(echo -e "${line}" | cut -f 1)
        count=$(echo -e "${line}" | cut -f 2)
        cmd="INSERT INTO sms.sms_financial(mobile,${col}) VALUES ('${mobile}',${count}) ON DUPLICATE KEY UPDATE ${col}=${count};"
        eval $(mysql -uroot -p111 --default-character-set=utf8 -e "${cmd}")
        echo "mobile:${mobile},count:${count}"
    done
}

path="/home/q/l-sms.monitor2.wap.cn6.xxx.com"
eval cd ${path}

line=$(find ${path} -type f)
for s in ${line[@]}
do
    col=$(echo ${s} |cut -d "/" -f5)
    insertIntoMysql ${s} ${col}
done
exit;