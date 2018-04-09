#!/usr/bin/env bash

#expression 和方括号([ ])之间必须有空格，否则会有语法错误。

a=10
b=20

if [ $a == $b ]
then
echo "a is equal to b"
fi
if [ $a != $b ]
then
echo "a is not equal to b"
fi

if [ $a == $b ]
then
echo "a is equal to b"
else
echo "a is not equal to b"
fi

if [ $a == $b ]
then
echo "a is equal to b"
elif [ $a -gt $b ]
then
echo "a is greater than b"
elif [ $a -lt $b ]
then
echo "a is less than b"
else
echo "None of the condition met"
fi

#if ... else 语句也经常与 test 命令结合使用，
# test 命令用于检查某个条件是否成立，与方括号([ ])类似。
num1=$[2*3]
num2=$[1+5]
if test $[num1] -eq $[num2]
then
echo 'The two numbers are equal!'
else
echo 'The two numbers are not equal!'
fi