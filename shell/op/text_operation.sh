#!/usr/bin/env bash

# 1. 遍历文件每一行，每一列
path="/home/q/qingming_msg_no_receive_count"
cat ${path} | while read line
do

    #获取第二列内容，注意一点加-e，处理特殊字符
    mobile=$(echo -e "${line}" | cut -f 2)
    echo "${mobile}"

    #for循环遍历每一列，然后获取值
    a=1
    for s in ${line[@]}
    do
        if [ ${a} != 1 ]; then
            count=${s}
        fi

        if [ ${a} == 1 ]; then
            mobile=${s}
            a=0
        fi
    done
    echo "${mobile},${count}"
done


# 2. 按列获取内容
function cl(){
        if [ $# -eq 2 ] ;  then
                awk -F "[$1]" -v col=$2  '{print $col}'
        else
                awk -v col=$1 '{print $col}'
        fi
}
#使用: cat file | cl 1   取第一列

# 3.如果列少可以这样
while read col1 col2 col3
do
    #读取每一行,将行的每一列分配给col1 col2 col3
    echo "${var1},${var2},${var3}"
done < ${log}
