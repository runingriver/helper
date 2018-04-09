#!/usr/bin/env bash

#expr 是一款表达式计算工具，使用它能完成表达式的求值操作。
val=`expr 2 + 2`
echo "Total value : $val"

#+-*/%
a=10
b=20
val=`expr $a + $b`
echo "a + b : $val"
val=`expr $a - $b`
echo "a - b : $val"
val=`expr $a \* $b`
echo "a * b : $val"
val=`expr $b / $a`
echo "b / a : $val"
val=`expr $b % $a`
echo "b % a : $val"
if [ $a == $b ]
then
echo "a is equal to b"
fi
if [ $a != $b ]
then
echo "a is not equal to b"
fi