#!/usr/bin/env bash

#分段获取hive数据,使用file+java方式导HBase
echo '-----------开始push hive to file----------------'
eval echo "${0},${1},${2},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
if [ -z "${1}" -o -z "${2}" ]; then
    echo "add param,like:20170101 20170201"
    exit
fi

eval cd $(dirname $0)
current_dir=$(pwd)
HIVE_SETTING="
SET mapreduce.reduce.memory.mb=8192;
SET mapreduce.reduce.java.opts='-Xmx8192M';
SET mapreduce.map.memory.mb=8192;
SET mapreduce.map.java.opts='-Xmx8192M';
SET mapred.child.java.opts=-Xmx8192m;
SET mapred.child.map.java.opts='-Xmx8192M';
SET mapred.map.tasks.speculative.execution=false;
SET mapred.reduce.tasks.speculative.execution=false;
SET mapred.job.priority=HIGH;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.dynamic.partition=true;
SET hive.exec.max.dynamic.partitions=100000;
SET hive.exec.max.dynamic.partitions.pernode=100000;
USE wirelessdata;
set mapred.job.queue.name=xxxdb;
set hive.exec.compress.output=true;
set mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec;
set hive.exec.parallel=true;
set mapred.job.name = ${0}_xxx;
"
start_date="${1}"
start_seconds=$(date -d "${start_date}" "+%s")
end_date="${2}"
end_seconds="$(date -d "${end_date}" "+%s")"
max_seconds=`expr ${end_seconds} - ${start_seconds}`
echo "start_date:${start_date},end_date:${end_date},max_second:${max_seconds}"

count=0
while : ; do
    dir_content="$(ls ${current_dir} | grep 'push')"
    echo "content: ${dir_content}"
    if [ "${dir_content}" != "" ] ; then
        echo "sleep 600 seconds"
        sleep 600
        continue
    fi

    for (( i=0; i<=9; i++ )) ; do
        date1="$(date -d "${start_date} ${count} day +" "+%Y%m%d")"
        seconds1="$(date -d "${date1}" "+%s")"
        diff=`expr ${seconds1} - ${start_seconds}`
        if [ ${max_seconds} -lt ${diff} ] ; then
            echo "execute finish.end time:$(date "+%Y-%m-%d %H:%M:%S")"
            break
        fi
        file_path="${current_dir}/push_${date1}"
        echo "date:${date1},path:${file_path},diff:${diff}"
        hive -e "
        ${HIVE_SETTING}
        select xxx \
        from xxx where num=${date1};
        " > ${file_path}
        count=`expr ${count} + 1`
        echo "finish get ${data1} data,count:${count},time:$(date "+%Y-%m-%d %H:%M:%S")"
    done
    code=$(curl --data-urlencode "path=${current_dir}" "http://l-xxx.com:9191/hive/push" 2>/dev/null)
    echo "result:${date1}:${code}"

    if [ ${max_seconds} -lt ${diff} ] ; then
        echo "execute finish.end time:$(date "+%Y-%m-%d %H:%M:%S")"
        break
    fi
done

echo "success, end time:$(date "+%Y-%m-%d %H:%M:%S")"
echo '-----------结束hive to file to HBase---------------'
