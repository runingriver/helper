#!/usr/bin/env bash

#echo "host:$1,username:$2,password:$3"


#1. 定义数据库基本信息
port=3306
host="localhost"
username="root"
password="111"

#2. 执行语句

sql="SELECT * FROM sms.ss_role"
path="/tmp/rst.txt"

if [ -z "$sql" ]
then
echo "sql语句为空"
exit
fi

if [ -n "$path" ]
then
if [ ! -e "$path" ]; then
echo "create the file:$path"
touch "$path"
fi
fi

#mysql -h${host}  -P${port}  -u${username} -p${password} --default_character_set=utf8 -e "${sql}" >${path}