#!/usr/bin/env bash


#shell中执行shell一般用source,fork,exec
#这里使用source,执行完子shell后继续执行父shell,公用shell所有变量,环境变量等.
cd /home/q/mongo/bin/
. ./mongo l-smsmongo1.wap.cn2.xxx.com:30000/sms export.js > /home/q/temp_mongo/outbox
#或:source mongo l-smsmongo1.wap.cn2.xxx.com:30000/sms export.js > /home/q/temp_mongo/outbox