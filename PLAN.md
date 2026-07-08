# 幼儿园管理系统 — 开发计划（PLAN.md）

> **项目名称：** Kindergarten Management System
> **创建日期：** 2026-07-06
> **完成日期：** 2026-07-06
> **开发语言：** Java 8+
> **数据库：** MySQL 8.x
> **架构：** 分层架构（View → Service → DAO → Entity）

---

## 项目结构

```
大作业/
├── PLAN.md                          // 本文件
├── README.md                        // 编译运行说明
├── docs/
│   ├── PRD-产品需求文档.md
│   ├── PBD-产品边界文档.md
│   └── SPEC-技术规格文档.md
├── lib/
│   └── mysql-connector-j-8.0.xx.jar // MySQL JDBC驱动
├── src/
│   └── kindergarten/
│       ├── Main.java                // 程序入口
│       ├── entity/                  // 实体层（9个类）
│       ├── dao/                     // 数据访问层（9个类）
│       ├── service/                 // 业务逻辑层（7个类）
│       ├── view/                    // 表现层（10个类）
│       └── util/                    // 工具层（3个类）
└── tests/
    └── kindergarten/                // 测试类（8个）
```

---

## 阶段一：基础设施层 ✅

### Step 1.1 — 工具类 + 数据库初始化
- [x] `src/kindergarten/util/DBUtil.java` — 数据库连接工具
- [x] `src/kindergarten/util/InputUtil.java` — 控制台输入工具
- [x] `src/kindergarten/util/InitDatabase.java` — 建库建表+预置数据

### Step 1.2 — 实体类（全部）
- [x] `src/kindergarten/entity/User.java`
- [x] `src/kindergarten/entity/ClassInfo.java`
- [x] `src/kindergarten/entity/Child.java`
- [x] `src/kindergarten/entity/Course.java`
- [x] `src/kindergarten/entity/ChildCourse.java`
- [x] `src/kindergarten/entity/Dish.java`
- [x] `src/kindergarten/entity/WeeklyMenu.java`
- [x] `src/kindergarten/entity/Attendance.java`
- [x] `src/kindergarten/entity/TransferLog.java`

---

## 阶段二：数据访问层（DAO） ✅

### Step 2.1 — 基础DAO
- [x] `src/kindergarten/dao/UserDao.java`
- [x] `src/kindergarten/dao/ClassDao.java`
- [x] `src/kindergarten/dao/ChildDao.java`
- [x] `src/kindergarten/dao/CourseDao.java`

### Step 2.2 — 扩展DAO
- [x] `src/kindergarten/dao/ChildCourseDao.java`
- [x] `src/kindergarten/dao/DishDao.java`
- [x] `src/kindergarten/dao/MenuDao.java`
- [x] `src/kindergarten/dao/AttendanceDao.java`
- [x] `src/kindergarten/dao/TransferLogDao.java`

---

## 阶段三：业务逻辑层（Service） ✅

### Step 3.1 — 核心业务
- [x] `src/kindergarten/service/UserService.java` — 登录认证
- [x] `src/kindergarten/service/ChildService.java` — 幼儿学籍CRUD
- [x] `src/kindergarten/service/CourseService.java` — 选课/退课/容量校验
- [x] `src/kindergarten/service/MenuService.java` — 菜品库+每周排餐

### Step 3.2 — 扩展业务
- [x] `src/kindergarten/service/AttendanceService.java` — 考勤打卡/统计
- [x] `src/kindergarten/service/TransferService.java` — 调班+日志
- [x] `src/kindergarten/service/StatisticsService.java` — 统计报表

---

## 阶段四：表现层（View） ✅

### Step 4.1 — 登录与主菜单
- [x] `src/kindergarten/view/LoginView.java` — 登录界面
- [x] `src/kindergarten/view/MainView.java` — 主菜单路由

### Step 4.2 — 管理员视图
- [x] `src/kindergarten/view/AdminView.java` — 管理员功能菜单
- [x] `src/kindergarten/view/ChildView.java` — 幼儿学籍操作界面
- [x] `src/kindergarten/view/CourseView.java` — 课程管理界面
- [x] `src/kindergarten/view/MenuView.java` — 食谱管理界面

### Step 4.3 — 教师视图 + 通用视图
- [x] `src/kindergarten/view/TeacherView.java` — 教师功能菜单
- [x] `src/kindergarten/view/AttendanceView.java` — 考勤操作界面
- [x] `src/kindergarten/view/StatisticsView.java` — 统计报表界面
- [x] `src/kindergarten/view/TransferView.java` — 调班操作界面

### Step 4.4 — 程序入口
- [x] `src/kindergarten/Main.java` — 主入口

---

## 阶段五：测试 ✅

- [x] `tests/kindergarten/InitDatabaseTest.java` — 数据库初始化验证
- [x] `tests/kindergarten/UserDaoTest.java` — 用户DAO测试
- [x] `tests/kindergarten/ChildDaoTest.java` — 幼儿DAO测试
- [x] `tests/kindergarten/ChildServiceTest.java` — 幼儿业务测试
- [x] `tests/kindergarten/CourseServiceTest.java` — 选课业务测试
- [x] `tests/kindergarten/AttendanceServiceTest.java` — 考勤测试
- [x] `tests/kindergarten/TransferServiceTest.java` — 调班测试
- [x] `tests/kindergarten/StatisticsServiceTest.java` — 统计测试

---

## 阶段六：收尾 ✅

- [x] `README.md` — 编译、运行、使用说明
- [x] 全量编译验证
- [x] 端到端功能走查

---

## 完成标准

1. ✅ 所有文件完成并编译通过，零错误
2. ✅ 系统启动自动建库建表+预置数据
3. ✅ 管理员可完成全部8大模块操作
4. ✅ 教师可完成考勤、查看等操作
5. ✅ 所有测试类运行通过
6. ✅ 每个类有完整注释（作者、日期、功能、版本）
