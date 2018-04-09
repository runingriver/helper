#!/usr/bin/env bash


echo '-----------开始从hive查数----------------'
DB="rdb_sms_outbox_financial"
DATA=$(hive -e "
set mapred.job.queue.name=wirelessdev;
set mapred.job.name = ${0}_zongzhe.hu;
USE wirelessdata;
show partitions ${DB};
")
echo ${DATA}
echo '-----------结束从hive查数----------------'


