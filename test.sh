#!/bin/bash
# 幼儿园管理系统 — 一键测试脚本
set -e

if [ ! -d "out" ]; then
    echo "未找到编译产物，请先运行 bash build.sh"
    exit 1
fi

TESTS=(
    "kindergarten.InitDatabaseTest"
    "kindergarten.UserDaoTest"
    "kindergarten.ChildDaoTest"
    "kindergarten.ChildServiceTest"
    "kindergarten.CourseServiceTest"
    "kindergarten.AttendanceServiceTest"
    "kindergarten.TransferServiceTest"
    "kindergarten.StatisticsServiceTest"
)

echo "══════ 运行全部测试 ══════"
PASSED=0
FAILED=0

for test in "${TESTS[@]}"; do
    echo ""
    echo "运行: $test"
    if java -Xmx256m -Xms64m -cp "out;lib/*" "$test"; then
        ((PASSED++))
    else
        ((FAILED++))
        echo "  ✗ 测试失败: $test"
    fi
done

echo ""
echo "══════════════════════════════════"
echo "  测试结果：$PASSED 通过 / $FAILED 失败"
echo "══════════════════════════════════"

if [ $FAILED -gt 0 ]; then
    exit 1
fi
