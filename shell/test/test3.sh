#!/usr/bin/env bash

echo "parent shell1"
PARENT="parent variable."

#source ./test4.sh
#echo "parent shell2. ${SUB}"
#
#. ./test4.sh
#echo "parent shell2. ${SUB}"

#C=$(source ./test3.1.sh)
#echo "${C}"
#echo "parent shell2. ${SUB}"
#
#B=$(. ./test3.1.sh)
#echo "${B}"
#echo "parent shell2. ${SUB}"

D=$(source ./test3.1.sh)
echo "result:${D}"
#source ./test3.1.sh
#echo "parent shell2,${SUB},${SUB2}"