#!/usr/bin/env bash

#case 语句匹配一个值或一个模式，如果匹配成功，执行相匹配的命令。

REGISTER(){
echo "register"
}
LOGIN(){
echo "login"
}
UPLOAD(){
echo "upload"
}
TYPE="register"
case ${TYPE} in
    register) REGISTER ;;
    login) LOGIN ;;
    upload) UPLOAD;;
    *) HELP;;
esac
exit 0

echo 'Input a number between 1 to 4'
echo 'Your number is:\c'
read aNum
case $aNum in
1) echo 'You select 1'
;;
2) echo 'You select 2'
;;
3) echo 'You select 3'
;;
4) echo 'You select 4'
;;
*) echo 'You do not select a number between 1 to 4'
;;
esac


