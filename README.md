# 幼儿园管理系统

> Java控制台应用程序 — 课程大作业

## 项目概述

幼儿园管理系统，实现幼儿学籍管理、课程管理、食谱管理、调班管理、考勤管理、数据统计等功能。

- **开发语言：** Java 8+
- **数据库：** MySQL 8.x
- **架构：** 分层架构（View → Service → DAO → Entity）
- **界面：** 控制台（Console）

## 环境要求

1. **JDK 8+** — `java -version` 确认可用
2. **MySQL 8.0+** — 确保MySQL服务已启动
3. **MySQL Connector/J** — JDBC驱动jar包

## 快速开始

### 1. 准备MySQL JDBC驱动

下载 `mysql-connector-j-8.0.xx.jar`，放入项目 `lib/` 目录。

下载地址：https://dev.mysql.com/downloads/connector/j/

### 2. 配置数据库连接

编辑 `src/kindergarten/util/DBUtil.java`，修改以下配置：

```java
private static final String USERNAME = "root";      // 你的MySQL用户名
private static final String PASSWORD = "lirui520";   // 你的MySQL密码（部署时需修改）
```

### 3. 编译

```bash
# 在项目根目录执行

# Windows (PowerShell):
javac -encoding UTF-8 -d out -cp "lib/*" (Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName })

# Windows (CMD):
for /r src %f in (*.java) do @echo %f | javac -encoding UTF-8 -d out -cp "lib/*" @files.txt

# Windows (Git Bash) / macOS/Linux:
javac -encoding UTF-8 -d out -cp "lib/*" src/kindergarten/**/*.java
```

### 4. 运行主程序

```bash
# Windows (PowerShell):
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main

# Windows (CMD):
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main

# macOS/Linux:
java -Xmx256m -Xms64m -cp "out:lib/*" kindergarten.Main
```

### 5. 运行测试

```bash
# 编译测试文件（PowerShell）
javac -encoding UTF-8 -d out -cp "out;lib/*" (Get-ChildItem -Path "tests" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName })

# 编译测试文件（Git Bash / macOS/Linux）
javac -encoding UTF-8 -d out -cp "out;lib/*" tests/kindergarten/*.java

# 运行单个测试（示例）
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.InitDatabaseTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.UserDaoTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.ChildDaoTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.ChildServiceTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.CourseServiceTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.AttendanceServiceTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.TransferServiceTest
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.StatisticsServiceTest
```

## 预置账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| teacher01 | 123456 | 教师（大一班） |
| teacher02 | 123456 | 教师（大二班） |
| teacher03 | 123456 | 教师（大三班） |
| teacher04 | 123456 | 教师（中一班） |
| teacher05 | 123456 | 教师（中二班） |
| teacher06 | 123456 | 教师（中三班） |
| teacher07 | 123456 | 教师（小一班） |
| teacher08 | 123456 | 教师（小二班） |
| teacher09 | 123456 | 教师（小三班） |

## 系统功能

### 管理员功能
1. **幼儿学籍管理** — 增删改查、按班级查看、按姓名搜索
2. **课程管理** — 增删课程、选课退课、容量校验
3. **食谱管理** — 菜品库管理、每周排餐、复制上周食谱
4. **调班管理** — 跨年级调班、调班记录
5. **考勤管理** — 查看考勤记录、出勤率统计
6. **数据统计** — 班级人数、课程选课、年级分布、出勤率

### 教师功能
1. 查看本班幼儿
2. 考勤打卡（支持逐个/批量）
3. 查看本班考勤记录
4. 查看课程安排
5. 查看本周食谱

## 项目结构

```
├── PLAN.md                         开发计划
├── README.md                       本文件
├── docs/                           项目文档
│   ├── PRD-产品需求文档.md
│   ├── PBD-产品边界文档.md
│   └── SPEC-技术规格文档.md
├── lib/                            第三方jar包
│   └── mysql-connector-j-8.0.xx.jar
├── src/                            源代码
│   └── kindergarten/
│       ├── Main.java               程序入口
│       ├── entity/                 实体层（9个类）
│       ├── dao/                    数据访问层（9个类）
│       ├── service/                业务逻辑层（7个类）
│       ├── view/                   表现层（10个类）
│       └── util/                   工具层（3个类）
└── tests/                          测试代码
    └── kindergarten/               8个测试类
```

## 注意事项

1. 首次运行会自动创建数据库和表，无需手动执行SQL
2. 如果表已存在，不会重复插入预置数据
3. 删除幼儿为软删除（标记离园），不会物理删除记录
4. 每个幼儿最多选4门兴趣课程
5. 调班支持跨年级（如从小班调到中班）
6. 控制台输入时请注意格式要求（日期：yyyy-MM-dd，性别：M/F）
