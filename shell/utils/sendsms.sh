#!/usr/bin/env bash

eval cd $(dirname $0)
eval echo "dir:$(pwd),Pid: $$"

account='xxxx'
file='1.txt'
message='带上TA，一定来！本君在十里桃林等你！http://d.xxxx.com/-nYlo6'

while read mobile
do

    code=$(curl -d "type=${account}&mobiles=${mobile}&message=${message}&groupid=tuiguang&hostname=l-sms2.f.cn1" "http://sms1.f.cn1.qunar.com/mon/req" 2>/dev/null)
    echo "${mobile}:${code}"
    sleep 0.001
done  < ${file}
