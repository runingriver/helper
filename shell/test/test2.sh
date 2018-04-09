#!/usr/bin/env bash


# 将outbox表中机票事业部的数据导入到hive的rdb_sms_outbox_flights

source /etc/profile

echo '-----------开始将outbox_flight表数据导入到hive----------------'
# 执行shell参数加上时间20170303,表示执行outbox_20170302表
echo "${0},${1}"
eval cd $(dirname $0)
currentDir=$(pwd)

_HIVE_TABLE=rdb_sms_outbox_flights

line="$(date -d "${1} 1 days ago" +'%Y%m%d')"
PATH_FILE="${currentDir}/flights_${line}"
echo "${PATH_FILE}"

mysql -hxxx -P3306 -uxxx -p'xxxx' sms -e "SET NAMES utf8; \
SELECT id,type,mobile,posttime,message,groupid,status,sent,optime,exectime,gid,retry, \
flag,channel,hostname,sendnum,md5,confirmtime,deliver,numbertype,gwconfig,countrysign, \
sedeliver,ivr,voicestatus,identify_type,identify_sub_type,signtype,autosign,ip,subaccount \
FROM outbox_${line} WHERE groupid IN  \
('qs_fland', 'qs_finter', 'insure', 'qt_order', 'qs_flag', 'qs_finter', 'qs_fland');"  \
| sed -e '1d' | sed 's/\r//g' > ${PATH_FILE}

gzip ${PATH_FILE}
PATH_GZ="${PATH_FILE}.gz"
echo "PATH_GZ:${PATH_GZ}"
hive -e "set mapreduce.job.name = ${0}_zongzhe.hu;USE wirelessdata; \
alter table ${_HIVE_TABLE} add IF NOT EXISTS PARTITION(num='${line}'); \
LOAD DATA LOCAL INPATH '${PATH_GZ}' OVERWRITE INTO TABLE ${_HIVE_TABLE} partition(num=${line});" || exit 1
rm -f ${PATH_FILE}
rm -f ${PATH_GZ}
echo "${line} success"

echo '-----------结束outbox_flights导hive----------------'
