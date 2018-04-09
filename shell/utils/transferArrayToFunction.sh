#!/usr/bin/env bash

#向函数传递数组：
#调用函数前，将数组转化为字符串。在函数中，读取字符串，并且分为数组。
fun() {
        #获取参数值,并分解
        local _arr=(`echo $1 | cut -d " "  --output-delimiter=" " -f 1-`)
        local _n_arr=${#_arr[@]}

        echo "数组:${_arr[@]}"
        echo "数组长度:$_n_arr"

        for((i=0;i<$_n_arr;i++));
        do
                elem=${_arr[$i]}
                echo "$i : $elem"
        done;
}

array=("a" "b" "c")

#$(echo ${array[@]})为一个字符串变量
fun "$(echo ${array[@]})"

#获取命令返回值
