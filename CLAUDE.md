# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

幼儿园管理系统 — Java 控制台应用（课程大作业）。管理幼儿学籍、课程、食谱、调班、考勤和数据统计。

## 构建与运行命令

```bash
# 编译（Git Bash / macOS/Linux）
javac -encoding UTF-8 -d out -cp "lib/*" src/kindergarten/**/*.java

# 编译（PowerShell）
javac -encoding UTF-8 -d out -cp "lib/*" (Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName })

# 运行主程序
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main

# 编译测试
javac -encoding UTF-8 -d out -cp "out;lib/*" tests/kindergarten/*.java

# 运行单个测试（示例）
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.ChildServiceTest
```

## 架构

分层架构，依赖方向严格单向：**View → Service → DAO → Entity**

- **entity/** — 纯数据类，对应数据库表。9 个实体：User, ClassInfo, Child, Course, ChildCourse, Dish, WeeklyMenu, Attendance, TransferLog
- **dao/** — 数据访问层，直接操作 JDBC。每个 DAO 通过 `DBUtil.getConnection()` 获取连接，手动管理资源关闭
- **service/** — 业务逻辑层，编排 DAO 调用。TransferService 包含事务管理。不直接操作数据库连接
- **view/** — 控制台 UI 层，负责输入输出和菜单路由。通过 `InputUtil` 处理用户输入
- **util/** — `DBUtil`（数据库连接）、`InputUtil`（控制台输入）、`InitDatabase`（首次运行自动建库建表+预置数据）

程序入口：`Main.java` → `MainView.start()` → 根据用户角色路由到 `AdminView` 或 `TeacherView`。

## 关键设计决策

- **数据库自动初始化**：首次运行时 `InitDatabase` 自动创建 `kindergarten` 数据库、9 张表并插入预置数据。使用 `CREATE IF NOT EXISTS`，已有数据时跳过
- **软删除**：幼儿删除为逻辑删除（`status` 字段标记离园），不物理删除
- **数据库配置**：在 `src/kindergarten/util/DBUtil.java` 中硬编码 USERNAME/PASSWORD，连接本地 MySQL 3306
- **选课限制**：每个幼儿最多选 4 门兴趣课程
- **无第三方框架**：纯 JDBC，无 ORM；无测试框架，测试类通过 `main` 方法 + 手动断言运行

## 表名前缀

所有表名以 `t_` 开头：`t_user`, `t_class_info`, `t_child`, `t_course`, `t_child_course`, `t_dish`, `t_weekly_menu`, `t_attendance`, `t_transfer_log`

## 预置账号

admin/admin123（管理员），teacher01~teacher09/123456（教师，各绑定一个班级）。
