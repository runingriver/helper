#!/usr/bin/env bash

#多表插入,根据mobile确定表名
# (sms_financial,sms_financial11,sms_financial0,...,sms_financial9)
getTableName(){
    mobile=${1}
    table="sms_financial"
    if [ ${mobile} -a -n ${mobile} ]
    then

        prefix=$(echo "${mobile:0:5}")
        #861开头的手机号太多,所以又分十张表
        if [ ${prefix:0:3} == "861" ]; then
            model=`expr ${prefix} % 10`
            table=${table}"${model}"
        fi

        #11位手机号的分一张sms_financial11
        if [ ${#mobile} == 11 -a ${mobile:0:1} == "1" ]; then
            table=${table}"11"
        fi
        echo "${table}"
    else
        #国际,其他的分一张sms_financial
        echo "${table}"
    fi
}


#将文本文件里面（mobile，count）字段插入到mysql中
insertIntoMysql(){
    path=${1}
    col=${2}
    echo "path:${path},column:${col}"
    cat ${path} | while read line
    do
        mobile=$(echo -e "${line}" | cut -f 1)
        count=$(echo -e "${line}" | cut -f 2)
        table=`getTableName ${mobile}`
        cmd="INSERT INTO sms.${table}(mobile,${col}) VALUES ('${mobile}',${count}) ON DUPLICATE KEY UPDATE ${col}=${count};"
        eval $(mysql -hxxx -P3306 -uroot -p'xxx' --default-character-set=utf8 -e "${cmd}")
        echo "table:${table},mobile:${mobile},count:${count}"
    done
}

path="/home/q/data_hive/hive1"
eval cd ${path}

line=$(find ${path} -type f)
for s in ${line[@]}
do
    col=$(echo ${s} |cut -d "/" -f6)
    insertIntoMysql ${s} ${col}
done
exit;


