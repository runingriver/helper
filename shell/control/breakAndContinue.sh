#!/usr/bin/env bash

for var1 in 1 2 3
do

for var2 in 0 5
do

if [ $var1 -eq 2 -a $var2 -eq 0 ]
then
break 2
else
echo "$var1 $var2"
fi

done
done

NUMS="1 2 3 4 5 6 7"
for NUM in $NUMS
do

Q=`expr $NUM % 2`
if [ $Q -eq 0 ]
then
echo "Number is an even number!!"
continue
fi

echo "Found odd number"
done

#while : 无限循环
#break n 表示跳出第n层循环。
while :
do
echo -n "Input a number between 1 to 5: "
read aNum
case $aNum in
1|2|3|4|5) echo "Your number is $aNum!"
;;
*) echo "You do not select a number between 1 to 5, game is over!"
break
;;
esac
done