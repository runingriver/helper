#!/usr/bin/env bash

set -x

function handleResponse(){
    #删除字符串中的{},并根据,和:分隔,输出前6列
	temp=$(echo $* |tr -d '{}' | awk -F'[,:]' '{print $1,$2,$3,$4,$5,$6}' )

    status=$(echo $temp | cut -d "\"" -f 3)

	if [ $status -eq  0 ]
	then
		case $2 in
		      'register')
			 echo "注册成功";;
		      'login')
			 echo "登陆成功";;
		      'homework')
			 echo "提交成功";;
		esac
	else
		echo "error"
		exit 1;
	fi
}

result='{"status":0,"message":null,"data":"success"}'
handleResponse $result 'register'


#启动java程序运行
function executeJava() {
#2017-02-16
time=`date -d "yesterday" +%Y-%m-%d`
LOG_FILE="/home/q/test_"+${time}
cd /home/q/
#设置当前shell的环境变量
export LC_ALL=en_US.UTF-8
sudo /home/q/java/jdk1.7.0_45/bin/java com/xxx/sms/main/Main > ${LOG_FILE} &
}


#显示所有shell读取的输入
set -v

x=100
ptrx=x
#eval将$ptrx 替换成x, 然后echo $x, \$表示一个$符号,因为两个$$连在一起会被翻译
eval echo \$$ptrx

#eval将$ptrx替换成x,命令就变成了x=50,重新赋值
eval $ptrx=50
echo $x