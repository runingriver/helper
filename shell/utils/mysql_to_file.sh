#!/usr/bin/env bash

eval cd $(dirname $0)
currentDir=$(pwd)
log="${currentDir}/sql.log"

#基本设置
table_name="outbox_20170320";

#查询
mysql -hxxxx -P3306 -uxxx -p'xxxx' -N -Dsms -e "SET NAMES utf8; \
SELECT type, exectime, mobile, message, groupid, hostname, sent, gid, subaccount, \
numbertype, channel, deliver, retry, ip, status, sendnum, confirmtime, posttime, \
optime, md5, flag, autosign, gwconfig, countrysign, signtype, sedeliver, ivr, \
voiceStatus, push_id, identify_content, identify_type, identify_sub_type, push_onlybunch, only_bunch \
FROM ${table_name};" > ${log}
