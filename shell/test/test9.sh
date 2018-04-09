#!/usr/bin/env bash

echo '-----------开始从hive查数----------------'
TIMESTAMP=$(date +%Y%m%d%H%M%S)
echo "PID: $$,start time:${TIMESTAMP}"

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
select a.mobile,
  if(b1.midnight_msg_no_receive_count>0, b1.midnight_msg_no_receive_count,0) as midnight_msg_no_receive_count,
  if(b2.am_msg_no_receive_count>0, b2.am_msg_no_receive_count,0) as am_msg_no_receive_count,
  if(b3.noon_msg_no_receive_count>0, b3.noon_msg_no_receive_count,0) as noon_msg_no_receive_count,
  if(b4.pm_msg_no_receive_count>0, b4.pm_msg_no_receive_count,0) as pm_msg_no_receive_count,
  if(b5.night_msg_no_receive_count>0, b5.night_msg_no_receive_count,0) as night_msg_no_receive_count,
  if(b6.weekday_msg_no_receive_count>0, b6.weekday_msg_no_receive_count,0) as weekday_msg_no_receive_count,
  if(b7.weekend_msg_no_receive_count>0, b7.weekend_msg_no_receive_count,0) as weekend_msg_no_receive_count,
  if(c1.first_ten_msg_no_receive_count>0,c1.first_ten_msg_no_receive_count,0) as first_ten_msg_no_receive_count,
  if(c2.middle_ten_msg_no_receive_count>0,c2.middle_ten_msg_no_receive_count,0) as middle_ten_msg_no_receive_count,
  if(c3.last_ten_msg_no_receive_count>0,c3.last_ten_msg_no_receive_count,0) as last_ten_msg_no_receive_count,
  if(d1.newyear_msg_no_receive_count>0,d1.newyear_msg_no_receive_count,0) as newyear_msg_no_receive_count,
  if(d2.springfestival_msg_no_receive_count>0,d2.springfestival_msg_no_receive_count,0) as springfestival_msg_no_receive_count,
  if(d3.qingming_msg_no_receive_count>0,d3.qingming_msg_no_receive_count,0) as qingming_msg_no_receive_count,
  if(d4.laborday_msg_no_receive_count>0,d4.laborday_msg_no_receive_count,0) as laborday_msg_no_receive_count,
  if(d5.duanwu_msg_no_receive_count>0,d5.duanwu_msg_no_receive_count,0) as duanwu_msg_no_receive_count,
  if(d6.moonfestival_msg_no_receive_count>0,d6.moonfestival_msg_no_receive_count,0) as moonfestival_msg_no_receive_count,
  if(d7.nationalday_msg_no_receive_count>0,d7.nationalday_msg_no_receive_count,0) as nationalday_msg_no_receive_count,
  if(e1.last_one_month_msg_no_receive_count>0,e1.last_one_month_msg_no_receive_count,0) as last_one_month_msg_no_receive_count,
  if(e2.last_three_month_msg_no_receive_count>0,e2.last_three_month_msg_no_receive_count,0) as last_three_month_msg_no_receive_count,
  if(e3.last_half_year_msg_no_receive_count>0,e3.last_half_year_msg_no_receive_count,0) as last_half_year_msg_no_receive_count,
  if(e4.last_one_year_msg_no_receive_count>0,e4.last_one_year_msg_no_receive_count,0) as last_one_year_msg_no_receive_count,
  if(e5.last_one_month_normal_msg_no_receive_count>0,e5.last_one_month_normal_msg_no_receive_count,0) as last_one_month_normal_msg_no_receive_count,
  if(e6.last_three_month_normal_msg_no_receive_count>0,e6.last_three_month_normal_msg_no_receive_count,0) as last_three_month_normal_msg_no_receive_count,
  if(e7.last_half_year_normal_msg_no_receive_count>0,e7.last_half_year_normal_msg_no_receive_count,0) as last_half_year_normal_msg_no_receive_count,
  if(e8.last_one_year_normal_msg_no_receive_count>0,e8.last_one_year_normal_msg_no_receive_count,0) as last_one_year_normal_msg_no_receive_count
from
  (select mobile from rdb_sms_outbox_financial where delivrd='UNDELIVRD' group by mobile) a left outer join
  (select mobile,count(mobile) as midnight_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and hour(optime) in (0,1,2,3,4,5,23) group by mobile) b1 on a.mobile=b1.mobile left outer join
  (select mobile,count(mobile) as am_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and hour(optime) in (6,7,8,9,10) group by mobile) b2 on a.mobile=b2.mobile left outer join
  (select mobile,count(mobile) as noon_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and hour(optime) in (11,12) group by mobile) b3 on a.mobile=b3.mobile left outer join
  (select mobile,count(mobile) as pm_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and hour(optime) in (13,14,15,16,17) group by mobile) b4 on a.mobile=b4.mobile left outer join
  (select mobile,count(mobile) as night_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and hour(optime) in (18,19,20,21,22) group by mobile) b5 on a.mobile=b5.mobile left outer join
  (select mobile,count(mobile) as weekday_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and pmod(datediff(optime, '2012-01-01'), 7) in (1,2,3,4,5) group by mobile) b6 on a.mobile=b6.mobile left outer join
  (select mobile,count(mobile) as weekend_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and pmod(datediff(optime, '2012-01-01'), 7) in (0,6) group by mobile) b7 on a.mobile=b7.mobile left outer join
  (select mobile,count(mobile) as first_ten_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and day(optime) in (1,2,3,4,5,6,7,8,9,10) group by mobile) c1 on a.mobile=c1.mobile left outer join
  (select mobile,count(mobile) as middle_ten_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and day(optime) in (11,12,13,14,15,16,17,18,19,20) group by mobile) c2 on a.mobile=c2.mobile left outer join
  (select mobile,count(mobile) as last_ten_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and day(optime) in (21,22,23,24,25,26,27,28,29,30,31) group by mobile) c3 on a.mobile=c3.mobile left outer join
  (select mobile,count(mobile) as newyear_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and to_date(optime) in ('2016-12-31','2017-01-01','2017-01-02','2016-01-01','2016-01-02','2016-01-03') group by mobile) d1 on a.mobile=d1.mobile left outer join
  (select mobile,count(mobile) as springfestival_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ((to_date(optime)>='2016-02-06' and to_date(optime)<='2016-02-14') or (to_date(optime)>='2017-01-27' and to_date(optime)<='2017-02-02')) group by mobile) d2 on a.mobile=d2.mobile left outer join
  (select mobile,count(mobile) as qingming_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and to_date(optime) in ('2015-04-04','2015-04-05','2015-04-06','2016-04-02','2016-04-03','2016-04-04') group by mobile) d3 on a.mobile=d3.mobile left outer join
  (select mobile,count(mobile) as laborday_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and to_date(optime) in ('2015-05-01','2015-05-02','2015-05-03','2016-04-30','2016-05-01','2016-05-02') group by mobile) d4 on a.mobile=d4.mobile left outer join
  (select mobile,count(mobile) as duanwu_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and to_date(optime) in ('2015-06-20','2015-06-21','2015-06-22','2016-06-09','2016-06-10','2016-06-11') group by mobile) d5 on a.mobile=d5.mobile left outer join
  (select mobile,count(mobile) as moonfestival_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and to_date(optime) in ('2015-09-26','2015-09-27','2016-09-15','2016-09-16','2016-09-17')group by mobile) d6 on a.mobile=d6.mobile left outer join
  (select mobile,count(mobile) as nationalday_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ((to_date(optime)>='2015-10-01' and to_date(optime)<='2015-10-07') or (to_date(optime)>='2016-10-01' and to_date(optime)<='2016-10-07')) group by mobile) d7 on a.mobile=d7.mobile left outer join
  (select mobile,count(mobile) as last_one_month_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr in (0,1,2) and to_date(optime)>='2017-03-01' and to_date(optime)<='2017-03-31' group by mobile) e1 on a.mobile=e1.mobile left outer join
  (select mobile,count(mobile) as last_three_month_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr in (0,1,2) and to_date(optime)>='2017-01-01' and to_date(optime)<='2017-03-31' group by mobile) e2 on a.mobile=e2.mobile left outer join
  (select mobile,count(mobile) as last_half_year_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr in (0,1,2) and to_date(optime)>='2016-10-01' and to_date(optime)<='2017-03-31' group by mobile) e3 on a.mobile=e3.mobile left outer join
  (select mobile,count(mobile) as last_one_year_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr in (0,1,2) and to_date(optime)>='2016-04-01' and to_date(optime)<='2017-03-31' group by mobile) e4 on a.mobile=e4.mobile left outer join
  (select mobile,count(mobile) as last_one_month_normal_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr=0 and to_date(optime)>='2017-03-01' and to_date(optime)<='2017-03-31' group by mobile) e5 on a.mobile=e5.mobile left outer join
  (select mobile,count(mobile) as last_three_month_normal_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr=0 and to_date(optime)>='2017-01-01' and to_date(optime)<='2017-03-31' group by mobile) e6 on a.mobile=e6.mobile left outer join
  (select mobile,count(mobile) as last_half_year_normal_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr=0 and to_date(optime)>='2016-10-01' and to_date(optime)<='2017-03-31' group by mobile) e7 on a.mobile=e7.mobile left outer join
  (select mobile,count(mobile) as last_one_year_normal_msg_no_receive_count from rdb_sms_outbox_financial where delivrd='UNDELIVRD' and ivr=0 and to_date(optime)>='2016-04-01' and to_date(optime)<='2017-03-31' group by mobile) e8 on a.mobile=e8.mobile;
"

hive -e "
${HIVE_SETTING}
${HIVE_SQL}
" >/home/q/data_to_hive/data_hive/data_hive_undelivrd

TIMESTAMP=$(date +%Y%m%d%H%M%S)
echo "end time:${TIMESTAMP}"
echo '-----------结束从hive查数----------------'
exit;

