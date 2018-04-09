#!/usr/bin/env bash

#循环到outbox取值,发送到接口，保存到HBase,导索引
echo '-----------开始hive to file to HBase----------------'
eval echo "${0},${1},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")"
eval cd $(dirname $0)
current_dir=$(pwd)

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
USE xxxx;
set mapred.job.queue.name=xxxx;
set hive.exec.compress.output=true;
set mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec;
set hive.exec.parallel=true;
set mapred.job.name = ${0}_xxx.xxx;
"

start_date="20170905"
start_seconds=$(date -d "${start_date}" "+%s")
end_seconds="$(date -d "20170919" "+%s")"
max_seconds=`expr ${end_seconds} - ${start_seconds}`

for (( i=0; i<=365; i++ )); do
    date1="$(date -d "${start_date} ${i} day +" "+%Y%m%d")"
    seconds1="$(date -d "${date1}" "+%s")"
    diff=`expr ${seconds1} - ${start_seconds}`
    if [ ${max_seconds} -lt ${diff} ] ; then
        echo "execute finish.end time:$(date "+%Y-%m-%d %H:%M:%S")"
        break
    fi

    file_path="${current_dir}/outbox_${date1}"
    echo "date:${date1},path:${file_path}"

    hive -e "
    ${HIVE_SETTING}
    select mobile,type,only_bunch from xxx where num=${date1};
    " > ${file_path}

    count=0
    while read line
    do
        code=$(curl --data-urlencode "data=${line}" "http://xxx.xxx.xxx.com:9191/shell/hive/index" 2>/dev/null)
        count=`expr ${count} + 1`
        echo "${count}:${code}"

        retry=1
        while [ -z "${code}" -o "${code}" != "ok" ] ; do
            if [ "${code}" == "error" ]; then
                break
            fi
            #failed和空都重试
            if [ ${retry} -gt 300 ]; then
                break
            fi
            retry=`expr ${retry} + 1`
            code=$(curl --data-urlencode "data=${line}" "http://xxx.xxx.xxx.com:9191/shell/hive/index" 2>/dev/null)
            echo "retry:${retry}:${code}"
            sleep 1
        done
    done < ${file_path}

    rm -f ${file_path}
done

echo "success, end time:$(date "+%Y-%m-%d %H:%M:%S")"
echo '-----------结束hive to file to HBase---------------'

