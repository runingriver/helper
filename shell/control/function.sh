#!/usr/bin/env bash

#函数返回值，可以显式增加return语句；如果不加，会将最后一条命令运行结果作为返回值。
#函数返回值只能是整数，一般用来表示函数执行成功与否，0表示成功，其他值表示失败。
#如果一定要让函数返回字符串，那么可以先定义一个变量，用来接收函数的计算结果，脚本在需要的时候访问这个变量来获得函数返回值。

#函数以动名词形式存储，且第二个单词首字母要大写

#定义函数
HelloWorld () {
echo "hello I am a function"
pwd
path=$?
echo "pwd返回值:$path"

res=`pwd`
echo "pwd返回值:$res"
}
# 调用函数,可以不用括号
HelloWorld

#传递参数
funWithParam(){
echo "The value of the first parameter is $1 !"
echo "The value of the second parameter is $2 !"
echo "The value of the tenth parameter is $10 !"
echo "The value of the tenth parameter is ${10} !"
echo "The value of the eleventh parameter is ${11} !"
echo "The amount of the parameters is $# !" # 参数个数
echo "The string of the parameters is $* !" # 传递给函数的所有参数
}
funWithParam 1 2 3 4 5 6 7 8 9 34 73


#函数嵌套调用
function_one () {
echo "hello world function1"
function_two
}
function_two () {
echo "hello world function2"
}
function_one

#函数返回值
funWithReturn(){
echo "The function is to get the sum of two numbers..."
echo -n "Input first number: "
read aNum
echo -n "Input another number: "
read anotherNum
echo "The two numbers are $aNum and $anotherNum !"
#return只能用来返回整数值
return $(($aNum+$anotherNum))
}
funWithReturn
#使用$?获取函数返回值
ret=$?
echo "The sum of two numbers is $ret !"


#下面是一些函数使用,示例
function uploadFile {
    #拼接字符串
	url=${host}\:${port}${uploadURL}
	result=$(curl -b "${currentDir}/${cookieFile}" -F file=@${currentDir}/demo1.sh ${url})
	#qwe,123:456,777:888 截取456赋给result_status变量
	result_status=$(echo ${result} | cut -d "," -f 1 | cut -d ":" -f 2)
	if [ "0" == "$result_status" ]
	then
		return 0
	else
		return 1
	fi
}
