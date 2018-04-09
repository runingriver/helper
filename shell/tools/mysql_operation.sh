#!/usr/bin/env bash

eval cd $(dirname $0)
currentDir=$(pwd)

DATA=$(date +%Y%m%d)

#命令格式:
# 执行命令: mysql -h[ip]  -u[user] -p[pass] -D[db name] -e "[mysql commands]"
# 执行文件中的命令: mysql -h[ip]  -u[user] -p[pass] -D[db name] < "file.sql"
#---------------------------------------------------------------------------------------------
#1. 最简单的方式
cmd="select count(*) from sms.temp_outbox";
cnt=$(mysql -uroot -p111 -s -e "${cmd}");
echo "Current count is : ${cnt}";
#---------------------------------------------------------------------------------------------

#2. 多条语句拼接,注意要指定数据库名.
#这里面有两个参数，-A、-N，-A的含义是不去预读全部数据表信息，这样可以解决在数据表很多的时候卡死的问题
#-N，很简单，Don't write column names in results，获取的数据信息省去列名称
MYSQL="mysql -h127.0.0.1 -uroot -p111 --default-character-set=utf8 -A -N"
sql="select * from sms.temp_outbox limit 100;"
result="$(${MYSQL} -e "${sql}")"

#这里要额外注意，echo -e "$result" > $dump_data的时候一定要加上双引号，不让导出的数据会挤在一行
log="${currentDir}/sql_${DATA}.log"
echo -e "${result}" > ${log}
#----------------------------------------------------------------------------------------

#3. 多条语句拼接,指定数据库,不适合数量比较多的情况!
mysql_prod="mysql -h127.0.0.1 -P3306 -uxxxx -p'xxxx' sms";
select_sql="SET NAMES utf8; SELECT type, exectime, mobile, message FROM temp_outbox;"
result="$(${mysql_prod} -e "${select_sql}")"
echo -e "${result}" > ${log}
#----------------------------------------------------------------------------------------

#4. 一条语句,指定数据库,sed -e '1d'表示删除第一列,sed 's/\r//g'处理Windows下的换行
OUT_FILE="${currentDir}/output.log"
mysql -hxxx -P3306 -uxxx -p'xxx' sms -e "SET NAMES utf8; \
SELECT id,type,mobile,posttime,message FROM outbox_20170303;" \
| sed -e '1d' | sed 's/\r//g' > ${PATH_FILE}
#----------------------------------------------------------------------------------------


#写入
mysql -hxxx -P3306 -uxxx -pxxx --default_character_set=utf8 -Dsms -e "\
LOAD DATA LOCAL INFILE '/home/zongzhehu/Downloads/xad' INTO TABLE sms.outbox_20170320;"
