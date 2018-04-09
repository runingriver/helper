#!/usr/bin/env bash


a=10
b=20

#非运算，表达式为 true 则返回 false，否则返回 true。
if [ $a != $b ]
then
echo "$a != $b : a is not equal to b"
else
echo "$a != $b: a is equal to b"
fi

#and 与运算，两个表达式都为 true 才返回 true。
if [ $a -lt 100 -a $b -gt 15 ]
then
echo "$a -lt 100 -a $b -gt 15 : returns true"
else
echo "$a -lt 100 -a $b -gt 15 : returns false"
fi

#or 或运算，有一个表达式为 true 则返回 true。
if [ $a -lt 100 -o $b -gt 100 ]
then
echo "$a -lt 100 -o $b -gt 100 : returns true"
else
echo "$a -lt 100 -o $b -gt 100 : returns false"
fi

#
if [ $a -lt 5 -o $b -gt 100 ]
then
echo "$a -lt 5 -o $b -gt 100 : returns true"
else
echo "$a -lt 5 -o $b -gt 100 : returns false"
fi