#!/usr/bin/env bash

#定时切割日志，压缩日志，切割日志

#set -x
source /etc/profile
current_dir=$(dirname $0)
pwd_dir=$(pwd)

if [ -z "${current_dir}" -o "${current_dir}" != "${pwd_dir}" ]
then
    current_dir='/opt/tiger/toutiao/app/content/arch/disque/'
fi

cd ${current_dir}

DEL_DATA=$(date -d "10 days ago"  +%F)
SLEEP_SECOND=$(echo $RANDOM | cut -c1-3)
LOG_DIR="${current_dir}disque-log"
for disque_dir in `find ${current_dir} -maxdepth 2 \( -type d -o -type l \) -name 'disque*'`
do
    for log_file in `find ${disque_dir} -maxdepth 1 -type f \( -name "*.log" -a ! -name "*.gz" \) -size 100M`
    do
        rotate_log="${log_file}${ZIP_DATE}"
        mv ${log_file} ${rotate_log} && touch ${log_file}
        gzip ${rotate_log}
    done

    #移动压缩文件到日志目录
#    find -L ${disque_dir} -maxdepth 1 -type f \( -name "*log*" -a -name "*.gz" \) -exec mv ${LOG_DIR} {} \;
    #删除10天前的日志文件
#    find -L ${disque_dir} -maxdepth 1 -type f \( -name "*log*" -a -name "*.gz" \) -ctime +10 -exec rm -f {} \;
done