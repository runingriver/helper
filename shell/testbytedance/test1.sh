#!/bin/bash

#set -x
source /etc/profile
current_dir=$(dirname $0)
pwd_dir=$(pwd)

if [ -z "${current_dir}" -o "${current_dir}" != "${pwd_dir}" ]
then
    current_dir='/opt/tiger/toutiao/app/content/arch/disque/disque-broker'
fi

cd ${current_dir}

echo "----------------------------------------------" >> ${current_dir}/run.log
echo "$(date "+%Y-%m-%d %H:%M:%S"):start disque node" >> ${current_dir}/run.log
eval echo "${0},${1},PID: $$,start time:$(date "+%Y-%m-%d %H:%M:%S")" >> ${current_dir}/run.log
eval echo "content.arch.disque,host:$(hostname),current dir:${current_dir}" >> ${current_dir}/run.log

exec `${current_dir}/disque-server ${current_dir}/disque.conf >> ${current_dir}/run.log 2>&1 &`

sleep 1
echo "$(date "+%Y-%m-%d %H:%M:%S"):start meet disque cluster" >> ${current_dir}/run.log
ip_list=`lh data.content.disque`
#ip_list='data.content.disque 10.22.44.149 10.22.44.91'
#local_ip='10.22.44.149'
local_ip=`hostname -I | sed 's/^ //g;s/ $//g'`
echo "ip list:${ip_list},local ip:${local_ip}" >> ${current_dir}/run.log


# 检查ip的合法性
check_ip(){
    regex_ip="(2[0-4][0-9]|25[0-5]|1[0-9][0-9]|[1-9]?[0-9])(\.(2[0-4][0-9]|25[0-5]|1[0-9][0-9]|[1-9]?[0-9])){3}"
    echo "$1" |grep -E "$regex_ip"
}

# 将node加入到集群中去
for ip in ${ip_list}
do
    result_ip=`check_ip ${ip}`
    # 判断是否是合法IP
    if [ -n "${result_ip}" -a "${result_ip}" = "${ip}" ]; then
        if [ "${result_ip}" = "${local_ip}" ]; then
            echo "result_ip:${result_ip},local_ip:${local_ip},shouldn't meet to cluster" >> ${current_dir}/run.log
        else
            echo "cluster ip:${result_ip} ~ ${ip}, meet to cluster" >> ${current_dir}/run.log
            eval `${current_dir}/disque -a "tmkNeXMzBmE1AQ" -p 7711 cluster meet ${ip} 7711 >> ${current_dir}/run.log 2>&1 &`
        fi
    fi
done



