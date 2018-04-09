#!/usr/bin/env bash

cd ~/github/helper/shell/

file="file-operator.sh"

# 检测文件是否可读，如果是，则返回 true。
if [ -r $file ]
then
echo "File has read access"
else
echo "File does not have read access"
fi

#检测文件是否可写，如果是，则返回 true。
if [ -w $file ]
then
echo "File has write permission"
else
echo "File does not have write permission"
fi

#检测文件是否可执行，如果是，则返回 true。
if [ -x $file ]
then
echo "File has execute permission"
else
echo "File does not have execute permission"
fi

#检测文件是否是普通文件（既不是目录，也不是设备文件），如果是，则返回 true。
if [ -f $file ]
then
echo "File is an ordinary file"
else
echo "This is sepcial file"
fi

#检测文件是否是目录，如果是，则返回 true。
if [ -d $file ]
then
echo "File is a directory"
else
echo "This is not a directory"
fi

#检测文件是否为空（文件大小是否大于0），不为空返回 true。
if [ -s $file ]
then
echo "File size is zero"
else
echo "File size is not zero"
fi

#检测文件（包括目录）是否存在，如果是，则返回 true。
if [ -e $file ]
then
echo "File exists"
else
echo "File does not exist"
fi

#http://www.shouce.ren/api/view/a/8169
#文件检验
#[ -d $file ] or [[ -d $file ]]	file为目录且存在时为真
#[ -e $file ] or [[ -e $file ]]	file为文件且存在时为真
#[ -f $file ] or [[ -f $file ]]	file为非目录普通文件存在时为真
#[ -s $file ] or [[ -s $file ]]	file文件存在, 且长度不为0时为真
#[ -L $file ] or [[ -L $file ]]	file为链接符且存在时为真
#[ -r $file ] or [[ -r $file ]]	file文件存在且可读时为真
#[ -w $file ] or [[ -w $file ]]	file文件存在且可写时为真
#[ -x $file ] or [[ -x $file ]]	file文件存在且可执行时为真


#列出某个目录下的所有文件
path="/home/q/l-sms.monitor2.wap.cn6.qunar.com"
#1. 包含绝对路径的文件名
line=$(find ${path} -type f)
for s in ${line[@]}
do
    echo "$s"
done
#2. 只包含文件名
$(ls ${path}) | while read file1
do

echo -e "${file1}"

done