#!/usr/bin/env bash

echo "进程Pid: $$"
#将文本文件里面（mobile，count）字段插入到mysql中
insertIntoMysql(){
    path=${1}
    col=${2}
    TIMESTAMP=$(date +%Y%m%d%H%M%S)
    echo "path:${path},column:${col},time:${TIMESTAMP}"
    str1="INSERT INTO sms.sms_financial99(mobile,${col}) VALUES "
    str2=" ON DUPLICATE KEY UPDATE ${col}=VALUES(${col});"
    n=0
    cat ${path} | while read line
    do
        mobile=$(echo -e "${line}" | cut -f 1)
        count=$(echo -e "${line}" | cut -f 2)
        let n++
        if [ `expr ${n} % 5000` == 0 ];
        then
            cmd=${cmd}"('${mobile}',${count})"
            cmd=${str1}${cmd}${str2}
            #echo ${cmd}
            eval $(mysql -h10.90.186.173 -P3306 -uroot -p'xxx' --default-character-set=utf8 -e "${cmd}")
            cmd=" "
        else
            cmd=${cmd}"('${mobile}',${count}),"
        fi
        #echo "mobile:${mobile},count:${count}"
    done
    TIMESTAMP=$(date +%Y%m%d%H%M%S)
    echo "end ${col} time:${TIMESTAMP}"
}
path="/home/q/data_hive/hive2"
eval cd ${path}

line=$(find ${path} -type f)
for s in ${line[@]}
do
    col=$(echo ${s} |cut -d "/" -f6)
    insertIntoMysql ${s} ${col}
done
exit;
#sudo ./hive1.sh 1>/home/q/data_hive/log1 2>/home/q/data_hive/error.log1 &
#sudo ./hive2.sh 1>/home/q/data_hive/log2 2>/home/q/data_hive/error.log2 &
#sudo ./hive3.sh 1>/home/q/data_hive/log3 2>/home/q/data_hive/error.log3 &
#sudo ./hive4.sh 1>/home/q/data_hive/log4 2>/home/q/data_hive/error.log4 &