#!/usr/bin/env bash
#
eval cd $(dirname $0)
#
SECOND=$(echo $RANDOM | cut -c1-2)
sleep ${SECOND}
#
DIR="`pwd`/leveldb"
echo "DIR:${DIR},date:$(date "+%Y-%m-%d %H:%M:%S")"
echo "$(find ${DIR} -maxdepth 1 -type f -mtime +10 -name "*.log*")"
#
find ${DIR} -maxdepth 1 -type f -mtime +10 -name "*.log" -exec rm -f {} \;