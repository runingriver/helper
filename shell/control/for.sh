#!/usr/bin/env bash

for loop in 1 2 3 4 5
do
echo "$loop"
done

for i in {4..800}
do
    echo "${i}"
done

for (( i=0; i<=9; i++ )) ; do
echo "${i}"
done

for str in "This is a string"
do
echo "$str"
done

line="abc 123 asd 456"
for s in ${line[@]}
do
    echo "$s"
done

#数组遍历
list=("qqq" "www" "eee" "rrr" "ttt")
for i in "${!list[@]}"
do
echo "${list[$i]}"
done

#另一种遍历,加不加引号都可以
for var in ${list[@]}
do
    echo $var
done

echo "while遍历"
i=0
while [ $i -lt ${#list[@]} ]
do
    echo ${list[$i]}
    let i++
done


for (( i=0; i<5; i=i+1 )); do
    echo $i
done