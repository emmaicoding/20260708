#!/bin/bash
# 幼儿园管理系统 — 一键运行脚本
set -e

if [ ! -d "out" ]; then
    echo "未找到编译产物，请先运行 bash build.sh"
    exit 1
fi

echo "启动幼儿园管理系统..."
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main
