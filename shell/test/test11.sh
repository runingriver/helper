#!/usr/bin/env bash

#批量导入手机号到手机黑名单的mysql表中
eval cd $(dirname $0)
currentDir=$(pwd)

echo "PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"

path="${currentDir}/mobile"

cat ${path} | while read line
do
    eval mobile=$(curl http://XXX/encrypt/mobile/${line} 2>/dev/null)
    SQL="INSERT INTO mobile_blacklist (group_ids, mobile) VALUES ('##87#','${mobile}');"
    eval $(mysql -hxxxx.xxx.x.x -P3306 -uXX -p'XXX' --default-character-set=utf8 -e "${SQL}")
done

echo "success, end time:$(date "+%Y-%m-%d %H:%M:%S")"
