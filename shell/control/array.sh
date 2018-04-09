#!/usr/bin/env bash

#括号来表示数组，数组元素用“空格”符号分割开
#使用@ 或 * 可以获取数组中的所有元素，例如：${array_name[*]}或${array_name[@]}

NAME[0]="000"
NAME[1]="111"
NAME[2]="222"
NAME[3]="333"
NAME[4]="444"
echo "First Index: ${NAME[0]}"
echo "All: ${NAME[*]}"
echo "All: ${NAME[@]}"

#数组长度
list=("000" "111" "222")
length=${#list[@]}
echo "$length"

length=${#list[*]}
echo "$length"

length=${#list[2]}
echo "$length"

#删除数组
unset list
#删除数据指定下标
unset list[1]
#eg
a=(1 2 3 4 5)
unset a[1]
echo ${a[*]}

# 分片
# 直接通过 ${数组名[@或*]:起始位置:长度} 切片原先数组，返回是字符串，中间用“空格”分开，因此如果加上”()”，将得到切片数组，上面例子：c 就是一个新数据。
a=(1 2 3 4 5)
echo ${a[@]:0:3}
1 2 3
echo ${a[@]:1:4}
2 3 4 5

c=(${a[@]:1:4})
echo ${#c[@]}
4
echo ${c[*]}
2 3 4 5


# 替换:调用方法是：${数组名[@或*]/查找字符/替换字符} 该操作不会改变原先数组内容，如果需要修改，可以看上面例子，重新定义数据。
a=(1 2 3 4 5)
echo ${a[@]/3/100}
1 2 100 4 5
echo ${a[@]}
1 2 3 4 5
a=(${a[@]/3/100})
echo ${a[@]}
1 2 100 4 5



