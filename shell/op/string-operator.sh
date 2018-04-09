#!/usr/bin/env bash

#!!! 在做判断的时候,变量一定要加双引号,eg: [ "$a" = "$b" ] 否则$a中如果是空,或有空格,或特殊字符,会被切成多个,从而报错~
getTableName(){
    mobile=${1}
    table="test_financial"
    #判断字符串是否为空,并且长度不为0 等价于 判断字符串不为空
    if [ ${mobile} -a -n ${mobile} ]
    then
        #字符串截取,获取手机号前5位
        prefix=$(echo "${mobile:0:5}")
        #判断手机号前3位是否是861
        if [ ${prefix:0:3} == "861" ]; then
            #手机号取前缀取模:86135 % 10 == 5
            model=`expr ${prefix} % 10`
            #结果table=test_financial5
            table=${table}"${model}"
        fi

        #判断长度是否为11,且第一个字符为1.
        if [ ${#mobile} == 11 -a ${mobile:0:1} == "1" ]; then
            table=${table}"11"
        fi
        echo "${table}"
    else
        echo "${table}"
    fi
}

mobile="86136Joi58171"
ret=`getTableName ${mobile}`
echo "The return is:$ret"
exit;

#字符串运算符
a="abc"
b="efg"

#检测两个字符串是否相等，相等返回 true。
if [ $a = $b ]
then
echo "$a = $b : a is equal to b"
else
echo "$a = $b: a is not equal to b"
fi

#检测两个字符串是否相等，不相等返回 true。
if [ $a != $b ]
then
echo "$a != $b : a is not equal to b"
else
echo "$a != $b: a is equal to b"
fi

#检测字符串长度是否为0，为0返回 true。
if [ -z $a ]
then
echo "-z $a : string length is zero"
else
echo "-z $a : string length is not zero"
fi

#检测字符串长度是否为0，不为0返回 true。
if [ -n $a ]
then
echo "-n $a : string length is not zero"
else
echo "-n $a : string length is zero"
fi

#检测字符串是否为空，不为空返回 true。
if [ $a ]
then
echo "$a : string is not empty"
else
echo "$a : string is empty"
fi

#字符串包含
#方法一
STR1="hello world."
result=$(echo ${STR1} | grep 'hello')
if [ "${result}" != ""  ]
then
echo "包含"
else
echo "不包含"
fi

#方法二
strA="helloworld"
strB="low"
if [[ ${strA} =~ ${strB} ]]
then
    echo "包含"
else
    echo "不包含"
fi
#方法三
A="helloworld"
B="low"
if [[ ${A} == *${B}* ]]
then
    echo "包含"
else
    echo "不包含"
fi

#方法4
thisString="1 2 3 4 5" # 源字符串
searchString="1 2" # 搜索字符串
case ${thisString} in
    *"${searchString}"*) echo "Enemy Spot" ;;
    *) echo "nope" ;;
esac



#单引号里的任何字符都会原样输出，单引号字符串中的变量是无效的；
# 单引号字串中不能出现单引号（对单引号使用转义符后也不行）。
# 双引号里可以有变量
#双引号里可以出现转义字符

#获取字符串长度
string="abcd"
echo ${#string} #输出 4

#提取字符串
string="alibaba is a great company"
echo ${string:1:4} #输出liba

#查找子字符串

#[expr index 字符串 字符]在字符串中发现字符的地方建立下标，或者标0
string="alibaba is a great company"
echo `expr index "$string" b` #输出4

#[expr length 字符串]字符串的长度
echo `expr length "$string"` #输出26

#[expr substr 字符串 偏移量 长度]替换字符串的子串，偏移的数值从 1 起计
echo `expr substr "$string" 1 7` #输出alibaba