#!/usr/bin/env bash

for (( i=0; i<5; i=i+1 )) do
code=$(curl www.baidu.com 2>/dev/null)
echo "${code}"
done



awk 'BEGIN{srand()}{b[rand()NR]=$0}END{for(x in b)print b[x]}'  1 > rsort


awk 'BEGIN{srand()}{b[rand()NR]=$0}END{for(x in b)print b[x]}' sort.txt > rsort