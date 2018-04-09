#!/usr/bin/env bash

#将txt中的数据导入到mysql表中
eval echo "${0},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
current_dir=$(pwd)

file_path='/home/hzz/Documents/code2'
count=0
while read line
do
    count=`expr ${count} + 1`
    status=$(echo -e "${line}" | cut -d '#' -f 1)
    desc=$(echo -e "${line}" | cut -d '#' -f 2)
    echo "count:${count}   ${status},${desc}"

#    mysql -h127.0.0.1 --default_character_set=utf8 -uroot -p'111' test_db -P3306 -e "SET NAMES utf8; \
#    INSERT INTO status_code (status, description) VALUE ('${status}', '${desc}');"
done < ${file_path}

#file_path='/home/hzz/Documents/code3'
#count=0
#while read line
#do
#    count=`expr ${count} + 1`
#    deliver=$(echo -e "${line}" | awk -F'#' '{print $1}')
#    desc=$(echo -e "${line}" | awk -F'#' '{print $2}')
#    echo "count:${count}   ${deliver},${desc}"
#
#    mysql -h127.0.0.1 --default_character_set=utf8 -uroot -p'111' test_db -P3306 -e "SET NAMES utf8; \
#    INSERT INTO status_code (deliver, description) VALUE ('${deliver}', '${desc}');"
#done < ${file_path}














