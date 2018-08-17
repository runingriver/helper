#!/usr/bin/env bash

set -x

current_dir=$(dirname $0)
pwd_dir=$(pwd)

if [ -z "${current_dir}" -o "${current_dir}" = "${pwd_dir}" ]
then
    echo "path note equal"
    current_dir='/opt/tiger/toutiao/app/content/arch/disque/disque-broker'
fi
