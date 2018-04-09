#!/usr/bin/env bash

echo "sub shell.${PARENT}"

SUB="sub shell variable"

SUB2="sub shell export"
export SUB2

echo "sub shell..."