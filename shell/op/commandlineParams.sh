#!/usr/bin/env bash

#shift 丢弃命令行参数中最左边的一个,将并右移
#shift 2 表示丢弃左边的两个参数

#执行./commandlineParams.sh 1 2 3 4
function normalParseCMD(){
until [ $# -eq 0 ]
do
        echo "第一个参数为: $1 参数个数为: $#"
        shift
done
}
normalParseCMD 1 2 3 4
#end--------------------------------------

#执行./commandlineParams.sh -a arg -b -c或./commandlineParams.sh -a arg -bc

#1. 选项参数的格式必须是-d val，而不能是中间没有空格的-dval.
#2. 所有选项参数必须写在其它参数的前面，即 -a arg1 -b arg2 而不能 -a -b arg1 arg2
#3. 遇到选项参数结束标记--，中止解析后面内容，如果中间遇到非选项的命令行参数，后面的选项参数就都取不到了。
#4. 不支持长选项， 即--debug 之类的选项

function getoptsParseCMD(){
while getopts "a:bc" arg #选项后面的冒号表示该选项需要参数
do

case ${arg} in
a) echo "解析到a,其参数为:$OPTARG" #参数存在$OPTARG中
;;
b) echo "解析到b"
;;
c) echo "解析到c"
;;
?)  #当有不认识的选项的时候arg为?
echo "unkonw argument"
exit 1
;;
esac

done
}

rst=$(getoptsParseCMD -a arg -b -c)
echo ${rst}

rst=$(getoptsParseCMD -a arg -bc)
echo ${rst}
#end-----------------------------------------------------------------------


#参数最前面的：表示消除警告提示信息，字符后面的冒号表示该选项必须有自己的参数.
function getoptsCMD2(){
while getopts ":a:bc:" opt
do
        case $opt in
                a)
                echo "a的参数为:$OPTARG 位置:$OPTIND"
                ;;
                b)
                echo "b的位置: $OPTIND"
                ;;
                c)
                echo "c的位置: $OPTIND"
                ;;
                ?)
                echo "error"
                exit 1
        esac
done
echo "总参数个数:$OPTIND"
shift $(( $OPTIND-1 ))
echo "第一个参数为:$0"
echo "现在所有的参数:$*"
}
getoptsCMD2 -a 11 -b -c 6
#end-----------------------------------------------------------------------

#----------------------------------------------------------------------
# 拓展  支持长选项以及可选参数  用getop实现

# \ 表示换行
TEMP=`getopt -o ab:c:: --long a-long,b-long:,c-long:: \
     -n 'example.bash' -- "$@"`

if [ $? != 0 ] ; then echo "Terminating..." >&2 ; exit 1 ; fi


#set 会重新排列参数的顺序，也就是改变$1,$2...$n的值，这些值在getopt中重新排列过了
eval set -- "$TEMP"

#经过getopt的处理，下面处理具体选项。

while true ; do
        case "$1" in
                -a|--a-long) echo "Option a" ; shift ;;
                -b|--b-long) echo "Option b, argument \`$2'" ; shift 2 ;;
                -c|--c-long)
                        # c是一个可选参数
                        case "$2" in
                                "") echo "Option c, no argument"; shift 2 ;;
                                *)  echo "Option c, argument \`$2'" ; shift 2 ;;
                        esac ;;
                --) shift ; break ;;
                *) echo "Internal error!" ; exit 1 ;;
        esac
done

echo "Remaining arguments:"
for arg do
   echo '--> '"\`$arg'" ;
done

#./commandlineParams.sh -a -b arg arg1 -c
#命令重排后: -a -b arg -c -- arg1