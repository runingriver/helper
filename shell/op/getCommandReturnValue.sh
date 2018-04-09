#!/usr/bin/env bash

#返回0,代表pwd执行成功
pwd
path=$?
echo "pwd返回值:$path"

#方法1
res=`pwd`
echo "pwd返回值:$res"

#方法2
val=$(pwd)
echo "pwd返回值:$val"
