#!/usr/bin/env bash

# 将outbox表中机票事业部的数据导入到hive的rdb_sms_outbox_financial
source /etc/profile

echo '-----------开始将outbox_flight表数据导入到hive----------------'
# 执行shell参数加上时间20170303,表示执行outbox_20170302表
echo "${0},${1},start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
currentDir=$(pwd)

_HIVE_TABLE=rdb_sms_outbox_finance_bu

line="$(date -d "${1} 1 days ago" +'%Y%m%d')"
PATH_FILE="${currentDir}/finance_${line}"
echo "${PATH_FILE}"

mysql -hxxx -P3306 -uxx -p'xxx' sms -e "SET NAMES utf8; \
SELECT
  mobile,
  count(mobile)                                   AS count,
  substr(optime, 1, 13)                           AS send_time,
  if(deliver = 'DELIVRD', 'DELIVRD', 'UNDELIVRD') AS deliverd,
  ivr                                             AS type
FROM outbox_${line}
GROUP BY mobile, substr(optime, 1, 13), if(deliver = 'DELIVRD', 'DELIVRD', 'UNDELIVRD'), ivr;
" | sed -e '1d' | sed 's/\r//g' > ${PATH_FILE}

gzip ${PATH_FILE}
PATH_GZ="${PATH_FILE}.gz"
echo "PATH_GZ:${PATH_GZ}"
hive -e "set mapreduce.job.name = ${0}_zongzhe.hu;USE wirelessdata; \
alter table ${_HIVE_TABLE} add IF NOT EXISTS PARTITION(num='${line}'); \
LOAD DATA LOCAL INPATH '${PATH_GZ}' OVERWRITE INTO TABLE ${_HIVE_TABLE} partition(num=${line});" || exit 1
rm -f ${PATH_FILE}
rm -f ${PATH_GZ}
echo "${line} success, end time:$(date "+%Y-%m-%d %H:%M:%S")"

echo '-----------结束outbox_flights导hive----------------'




