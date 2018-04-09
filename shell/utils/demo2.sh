#!/usr/bin/env bash



line="$(date -d "${1} 1 days ago" +'%Y%m%d')"
echo "$line"

lines="89234"
PATH_FILE="outbox_${lines}"

echo "$PATH_FILE"





local=$(pwd)
echo "local:${local}"

echo "$0"
currentDir=$(cd $(dirname $0);pwd)

echo "目录:$currentDir, 进程Pid: $$"

eval echo "$currentDir $(pwd)"

#cd /home/q/scp/221-222-223
eval echo $(pwd)

