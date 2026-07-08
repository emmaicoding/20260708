#!/bin/bash
# 幼儿园管理系统 — 一键编译脚本
set -e

echo "══════ 编译幼儿园管理系统 ══════"

# 检查 JDK
if ! command -v javac &> /dev/null; then
    echo "✗ 未找到 javac，请安装 JDK 8+"
    exit 1
fi

# 清理旧编译产物
rm -rf out
mkdir -p out

# 编译源码
echo "正在编译源码..."
javac -encoding UTF-8 -d out -cp "lib/*" src/kindergarten/**/*.java
echo "✓ 源码编译完成"

# 编译测试
echo "正在编译测试..."
javac -encoding UTF-8 -d out -cp "out;lib/*" tests/kindergarten/*.java
echo "✓ 测试编译完成"

echo "══════ 编译成功 ══════"
