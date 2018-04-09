#!/usr/bin/env bash

#根据access日志统计接口的QPS
#预设格式:[26/Sep/2017:14:59:17 +0800] 10.88.65.39 "POST /shell/hive HTTP/1.1" 200 (6 ms)
# $1:[26/Sep/2017:14:59:17  $2:+0800] ....   $5:/shell/hive

grep '/shell/hive' access_log.2017-09-26.log | cut -d ":" -f 2,3 |sort|uniq -c| awk '{qps=$1/60;print $2,$1,qps}'
