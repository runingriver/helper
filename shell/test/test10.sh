#!/usr/bin/env bash

# 将outbox表中指定字段数据导入到hive的rdb_sms_outbox_financial
source /etc/profile

echo '-----------开始将outbox表数据导入到hive----------------'
# 执行shell参数加上时间20170303,表示执行outbox_20170302表
eval echo "${0},${1},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
currentDir=$(pwd)

_HIVE_TABLE=rdb_sms_outbox_financial

line="$(date -d "${1} 1 days ago" +'%Y%m%d')"
PATH_FILE="${currentDir}/finance_${line}"
echo "${PATH_FILE}"

mysql -hxxx -P3306 -uxxx -p'xxx' sms -e "SET NAMES utf8; \
select mobile,optime,if(deliver='DELIVRD','DELIVRD','UNDELIVRD') as deliver,ivr
FROM outbox_${line};
" | sed -e '1d' | sed 's/\r//g' > ${PATH_FILE}

gzip ${PATH_FILE}
PATH_GZ="${PATH_FILE}.gz"
echo "PATH_GZ:${PATH_GZ}"
hive -e "set mapreduce.job.name = ${0}_zongzhe.hu;USE wirelessdata; \
alter table ${_HIVE_TABLE}  add IF NOT EXISTS PARTITION(num='${line}'); \
LOAD DATA LOCAL INPATH '${PATH_GZ}' OVERWRITE INTO TABLE ${_HIVE_TABLE} partition(num=${line});" || exit 1
rm -f ${PATH_FILE}
rm -f ${PATH_GZ}
echo "${line} success, end time:$(date "+%Y-%m-%d %H:%M:%S")"

echo '-----------结束outbox导hive----------------'






