#!/usr/bin/env bash

#查出mysql中的数据,然后根据数据去爬取json,解析,再然后更新到,mysql对应行上
#set -x
eval echo "${0},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
current_dir=$(pwd)

log="${current_dir}/sql.log"
mysql -hxxx --default_character_set=utf8 -uxxx -p'xxx' sms -P3306 -A -N -e "SET NAMES utf8;
select id,number from xxx where city='-' and mno=0 limit 10;" > ${log}

count=0
while read line
do
    id=$(echo -e "${line}" | cut -f 1)
    number=$(echo -e "${line}" | cut -f 2)
    code=$(curl -d "key=${number}1234" "http://decrypt.xxx.xxx.com/service/api/decryptPhone" 2>/dev/null)

    message=$(echo "${code}" | jq -r '.data.message')
    if [ "${message}" != "成功" ]; then
        echo "id:${id},number:${number},抓取失败!"
        continue
    fi

    count=`expr ${count} + 1`
    province=$(echo "${code}" | jq -r '.data.data.province')
    city=$(echo "${code}" | jq -r '.data.data.city')
    mno=$(echo "${code}" | jq -r '.data.data.mmo')

    if [ "${mno}" == "1" ]; then
        type="中国移动"
    elif [ "${mno}" == "2" ]; then
        type="中国联通"
    elif [ "${mno}" == "3" ]; then
        type="中国电信"
    fi

    mysql -hxxx --default_character_set=utf8 -uxxx -p'xxx' sms -P3306 -A -N -e \
    "SET NAMES utf8;update xxx set provc='${province}',city='${city}',mno='${mno}',type='${type}' where id=${id};"

    echo "result:${count}:${id},${number}:${code},${province},${city},${mno},${type}"
done < ${log}






