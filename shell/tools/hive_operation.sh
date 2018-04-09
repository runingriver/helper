#!/usr/bin/env bash

echo '-----------开始从hive查数----------------'
HIVE_SETTING="
SET mapred.child.java.opts=-Xmx8192m;
SET mapreduce.reduce.memory.mb=8192;
SET mapreduce.reduce.java.opts='-Xmx8192M';
SET mapreduce.map.memory.mb=8192;
SET mapreduce.map.java.opts='-Xmx8192M';
SET mapred.child.map.java.opts='-Xmx8192M';
SET mapred.job.priority=HIGH;
SET mapred.map.tasks.speculative.execution=false;
SET mapred.reduce.tasks.speculative.execution=false;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.dynamic.partition=true;
SET hive.exec.max.dynamic.partitions=100000;
SET hive.exec.max.dynamic.partitions.pernode=100000;
USE wirelessdata;
set mapred.job.queue.name=wirelessdev;
set hive.exec.compress.output=true;
set mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec;
set hive.exec.parallel=true;
set mapred.job.name = ${0}_zongzhe.hu;
"

HIVE_SQL="
select count(1) from rdb_sms_outbox;
"

#1. 将hive执行结果赋值给变量
DATA=$(hive -e "
${HIVE_SETTING}
${HIVE_SQL};
")

#2. 将hive结果输出到文件中
hive -e "
${HIVE_SETTING}
${HIVE_SQL};
" >/home/q/hive_data.txt

echo '-----------结束从hive查数----------------'