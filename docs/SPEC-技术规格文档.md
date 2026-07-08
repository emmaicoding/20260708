# 幼儿园管理系统 — 技术规格文档（SPEC）

> **文档版本：** v1.0
> **创建日期：** 2026-07-06
> **文档性质：** 课程大作业 — 技术设计规格说明

---

## 1. 系统架构设计

### 1.1 分层架构图

```
┌──────────────────────────────────────────────────────────────┐
│                      表现层 (View Layer)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │LoginView │ │ChildView │ │CourseView│ │MenuView  │  ...   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
├──────────────────────────────────────────────────────────────┤
│                      业务层 (Service Layer)                    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │
│  │ChildService   │ │CourseService  │ │MenuService   │  ...   │
│  └──────────────┘ └──────────────┘ └──────────────┘        │
├──────────────────────────────────────────────────────────────┤
│                      数据层 (DAO Layer)                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ChildDao  │ │CourseDao │ │MenuDao   │ │AttendDao │  ...   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
├──────────────────────────────────────────────────────────────┤
│                      实体层 (Entity Layer)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │  Child   │ │  Course  │ │  ClassInfo│ │Attendance│  ...   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
├──────────────────────────────────────────────────────────────┤
│                      工具层 (Util Layer)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐                 │
│  │DBUtil    │ │InputUtil │ │InitDatabase  │                 │
│  └──────────┘ └──────────┘ └──────────────┘                 │
├──────────────────────────────────────────────────────────────┤
│                     MySQL 数据库                               │
└──────────────────────────────────────────────────────────────┘
```

### 1.2 包结构设计

```
com.kindergarten
├── entity          // 实体类（数据模型）
│   ├── User.java
│   ├── Child.java
│   ├── ClassInfo.java
│   ├── Course.java
│   ├── ChildCourse.java
│   ├── Dish.java
│   ├── WeeklyMenu.java
│   ├── Attendance.java
│   └── TransferLog.java
│
├── dao             // 数据访问层（数据库操作）
│   ├── UserDao.java
│   ├── ChildDao.java
│   ├── ClassDao.java
│   ├── CourseDao.java
│   ├── DishDao.java
│   ├── MenuDao.java
│   ├── AttendanceDao.java
│   └── TransferLogDao.java
│
├── service         // 业务逻辑层
│   ├── UserService.java
│   ├── ChildService.java
│   ├── CourseService.java
│   ├── MenuService.java
│   ├── AttendanceService.java
│   ├── TransferService.java
│   └── StatisticsService.java
│
├── view            // 表现层（控制台界面）
│   ├── LoginView.java
│   ├── MainView.java
│   ├── AdminView.java
│   ├── TeacherView.java
│   ├── ChildView.java
│   ├── CourseView.java
│   ├── MenuView.java
│   ├── AttendanceView.java
│   └── StatisticsView.java
│
└── util            // 工具类
    ├── DBUtil.java
    ├── InputUtil.java
    └── InitDatabase.java
```

## 2. 数据库设计

### 2.1 ER关系概览

```
User ──────── 1:N ──── (独立表，无外键关联)

ClassInfo ─── 1:N ──── Child ──── N:M ──── Course
                          │                  │
                          │                  │
                          ▼                  ▼
                     Attendance        ChildCourse

Dish ──────── 1:N ──── WeeklyMenu

Child ─────── 1:N ──── TransferLog
ClassInfo ── 1:N ──── TransferLog
```

### 2.2 表结构定义

#### 表1：用户表 (t_user)

```sql
CREATE TABLE t_user (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(30) NOT NULL UNIQUE    COMMENT '用户名',
    password    VARCHAR(50) NOT NULL           COMMENT '密码（明文存储，课程作业级别）',
    real_name   VARCHAR(30) NOT NULL           COMMENT '真实姓名',
    role        TINYINT NOT NULL DEFAULT 2     COMMENT '角色：1=管理员 2=教师',
    class_id    INT DEFAULT NULL               COMMENT '教师负责班级ID（管理员为NULL）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class_info(id)
) COMMENT '用户表';
```

#### 表2：班级表 (t_class_info)

```sql
CREATE TABLE t_class_info (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name  VARCHAR(20) NOT NULL UNIQUE    COMMENT '班级名称（如：大一班）',
    grade       VARCHAR(10) NOT NULL           COMMENT '年级（大班/中班/小班）',
    max_count   INT NOT NULL DEFAULT 10        COMMENT '班级最大人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '班级信息表';
```

#### 表3：幼儿表 (t_child)

```sql
CREATE TABLE t_child (
    id              INT PRIMARY KEY AUTO_INCREMENT COMMENT '幼儿ID',
    name            VARCHAR(50) NOT NULL           COMMENT '姓名',
    gender          CHAR(1) NOT NULL               COMMENT '性别：M=男 F=女',
    birth_date      DATE NOT NULL                  COMMENT '出生日期',
    parent_name     VARCHAR(50) NOT NULL           COMMENT '家长姓名',
    parent_phone    VARCHAR(20) NOT NULL           COMMENT '家长电话',
    class_id        INT NOT NULL                   COMMENT '所在班级ID',
    enrollment_date DATE NOT NULL                  COMMENT '入园日期',
    status          TINYINT NOT NULL DEFAULT 1     COMMENT '状态：1=在园 0=离园',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class_info(id)
) COMMENT '幼儿信息表';
```

#### 表4：课程表 (t_course)

```sql
CREATE TABLE t_course (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(50) NOT NULL UNIQUE    COMMENT '课程名称',
    max_count   INT NOT NULL DEFAULT 15        COMMENT '课程最大人数',
    description VARCHAR(200) DEFAULT NULL      COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '兴趣课程表';
```

#### 表5：幼儿选课表 (t_child_course)

```sql
CREATE TABLE t_child_course (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id    INT NOT NULL                   COMMENT '幼儿ID',
    course_id   INT NOT NULL                   COMMENT '课程ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    UNIQUE KEY uk_child_course (child_id, course_id) COMMENT '同一幼儿不可重复选同一课程',
    FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES t_course(id)
) COMMENT '幼儿选课关系表';
```

#### 表6：菜品表 (t_dish)

```sql
CREATE TABLE t_dish (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    dish_name   VARCHAR(50) NOT NULL           COMMENT '菜品名称',
    dish_type   TINYINT NOT NULL               COMMENT '类型：1=主食 2=荤菜 3=素菜 4=汤 5=水果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '菜品库表';
```

#### 表7：每周食谱表 (t_weekly_menu)

```sql
CREATE TABLE t_weekly_menu (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    week_start  DATE NOT NULL                  COMMENT '周起始日期（周一）',
    day_of_week TINYINT NOT NULL               COMMENT '星期几：1=周一 ... 5=周五',
    meal_type   TINYINT NOT NULL               COMMENT '餐次：1=早餐 2=午餐 3=晚餐',
    dish_id     INT NOT NULL                   COMMENT '菜品ID',
    FOREIGN KEY (dish_id) REFERENCES t_dish(id)
) COMMENT '每周食谱表';
```

#### 表8：考勤表 (t_attendance)

```sql
CREATE TABLE t_attendance (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id    INT NOT NULL                   COMMENT '幼儿ID',
    attend_date DATE NOT NULL                  COMMENT '考勤日期',
    status      TINYINT NOT NULL               COMMENT '状态：1=出勤 2=缺勤 3=请假 4=迟到',
    remark      VARCHAR(100) DEFAULT NULL      COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    UNIQUE KEY uk_child_date (child_id, attend_date) COMMENT '同一幼儿每天一条记录',
    FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE
) COMMENT '考勤记录表';
```

#### 表9：调班记录表 (t_transfer_log)

```sql
CREATE TABLE t_transfer_log (
    id              INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id        INT NOT NULL                   COMMENT '幼儿ID',
    old_class_id    INT NOT NULL                   COMMENT '原班级ID',
    new_class_id    INT NOT NULL                   COMMENT '新班级ID',
    operator_id     INT NOT NULL                   COMMENT '操作人ID',
    transfer_date   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调班时间',
    remark          VARCHAR(100) DEFAULT NULL      COMMENT '备注',
    FOREIGN KEY (child_id) REFERENCES t_child(id),
    FOREIGN KEY (old_class_id) REFERENCES t_class_info(id),
    FOREIGN KEY (new_class_id) REFERENCES t_class_info(id),
    FOREIGN KEY (operator_id) REFERENCES t_user(id)
) COMMENT '调班记录表';
```

## 3. 核心类设计

### 3.1 实体类示例 — Child.java

```java
package com.kindergarten.entity;

import java.time.LocalDate;

/**
 * 幼儿实体类
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 对应数据库 t_child 表，存储幼儿基本信息
 */
public class Child {
    private Integer id;            // 幼儿ID
    private String name;           // 姓名
    private String gender;         // 性别：M/F
    private LocalDate birthDate;   // 出生日期
    private String parentName;     // 家长姓名
    private String parentPhone;    // 家长电话
    private Integer classId;       // 所在班级ID
    private String className;      // 所在班级名称（关联查询用，非数据库字段）
    private LocalDate enrollmentDate; // 入园日期
    private Integer status;        // 状态：1在园 0离园

    // getter/setter 省略
}
```

### 3.2 DAO层示例 — ChildDao.java

```java
package com.kindergarten.dao;

import com.kindergarten.entity.Child;
import java.util.List;

/**
 * 幼儿数据访问对象
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 封装幼儿表的数据库CRUD操作
 */
public class ChildDao {

    /**
     * 添加幼儿
     * @param child 幼儿实体对象
     * @return 新增记录的ID
     */
    public int insert(Child child) { ... }

    /**
     * 根据ID查询幼儿（含班级名称）
     * @param id 幼儿ID
     * @return 幼儿实体，不存在返回null
     */
    public Child selectById(int id) { ... }

    /**
     * 根据班级ID查询所有在园幼儿
     * @param classId 班级ID
     * @return 幼儿列表
     */
    public List<Child> selectByClassId(int classId) { ... }

    /**
     * 修改幼儿信息（不含班级，班级通过调班接口修改）
     * @param child 幼儿实体
     * @return 影响行数
     */
    public int update(Child child) { ... }

    /**
     * 软删除幼儿（设置status=0）
     * @param id 幼儿ID
     * @return 影响行数
     */
    public int delete(int id) { ... }
}
```

### 3.3 Service层示例 — CourseService.java

```java
package com.kindergarten.service;

import com.kindergarten.dao.CourseDao;
import com.kindergarten.dao.ChildCourseDao;

/**
 * 课程业务逻辑层
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 处理课程相关业务：选课、退课、容量校验等
 */
public class CourseService {
    private CourseDao courseDao = new CourseDao();
    private ChildCourseDao childCourseDao = new ChildCourseDao();

    /**
     * 为幼儿选课
     * 业务规则：
     *   1. 每人最多选4门课
     *   2. 不可重复选同一门课
     *   3. 课程人数未达上限
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 选课结果描述
     */
    public String selectCourse(int childId, int courseId) {
        // 1. 查询该幼儿已选课程数
        int count = childCourseDao.countByChildId(childId);
        if (count >= 4) {
            return "选课失败：该幼儿已选满4门课程";
        }
        // 2. 检查是否重复选课
        if (childCourseDao.exists(childId, courseId)) {
            return "选课失败：该幼儿已选过此课程";
        }
        // 3. 检查课程容量
        int currentCount = childCourseDao.countByCourseId(courseId);
        int maxCount = courseDao.selectById(courseId).getMaxCount();
        if (currentCount >= maxCount) {
            return "选课失败：该课程已达人数上限（" + maxCount + "人）";
        }
        // 4. 执行选课
        childCourseDao.insert(childId, courseId);
        return "选课成功";
    }
}
```

### 3.4 工具类 — DBUtil.java

```java
package com.kindergarten.util;

import java.sql.*;

/**
 * 数据库连接工具类
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 提供MySQL数据库连接和资源关闭的统一管理
 */
public class DBUtil {
    private static final String URL      = "jdbc:mysql://localhost:3306/kindergarten?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 获取数据库连接
     * @return Connection对象
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭数据库资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) { ... }
}
```

## 4. 控制台交互设计

### 4.1 登录流程

```
╔══════════════════════════════════════╗
║      幼儿园管理系统 v1.0              ║
╠══════════════════════════════════════╣
║  请输入用户名：admin                  ║
║  请输入密码：******                   ║
║                                      ║
║  ✓ 登录成功！欢迎，管理员 张老师       ║
╚══════════════════════════════════════╝
```

### 4.2 管理员主菜单

```
╔══════════════════════════════════════╗
║          管理员功能菜单                ║
╠══════════════════════════════════════╣
║  1. 幼儿学籍管理                      ║
║  2. 班级管理                          ║
║  3. 课程管理                          ║
║  4. 食谱管理                          ║
║  5. 调班管理                          ║
║  6. 考勤管理                          ║
║  7. 数据统计                          ║
║  8. 修改密码                          ║
║  0. 退出登录                          ║
╚══════════════════════════════════════╝
请选择功能编号：
```

### 4.3 班级幼儿查询结果示例

```
═══════════════════════════════════════════════════
  大一班 幼儿名单（共10人）
═══════════════════════════════════════════════════
  编号  姓名    性别  出生日期     家长      联系电话
───────────────────────────────────────────────────
  1    张小明   男   2020-03-15  张大明    13800001111
  2    李小红   女   2020-05-22  李大红    13800002222
  3    王小华   男   2019-11-08  王大华    13800003333
  ...
═══════════════════════════════════════════════════
```

### 4.4 统计报表示例

```
═══════════════════════════════════════════════════
  班级人数统计
═══════════════════════════════════════════════════
  班级     年级   当前人数  男生  女生  容量
───────────────────────────────────────────────────
  大一班   大班     10      6     4    10
  大二班   大班     10      5     5    10
  大三班   大班      9      4     5    10
  中一班   中班     10      5     5    10
  ...
  ═══════════════════════════════════════════════
  合计              90     47    43    90
═══════════════════════════════════════════════════
```

## 5. 预置数据初始化

系统首次运行时，`InitDatabase` 类自动执行：

```java
/**
 * 数据库初始化类
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 首次运行时自动创建数据库、建表、插入预置数据
 */
public class InitDatabase {

    public void init() {
        // 1. 创建数据库 kindergarten（如不存在）
        // 2. 创建所有9张表（如不存在）
        // 3. 插入预置数据：
        //    - 9个班级（大一~大三、中一~中三、小一~小三）
        //    - 4门兴趣课程（舞蹈、跆拳道、钢琴、美术，容量各15人）
        //    - 1个管理员账号（admin / admin123）
        //    - 9个教师账号（teacher01~teacher09，对应9个班级）
        //    - 90个幼儿样本数据（每班10人）
        //    - 20个基础菜品
    }
}
```

## 6. 团队分工建议（5人）

| 角色 | 人数 | 负责模块 | 交付物 |
|------|------|---------|--------|
| 组长/架构师 | 1人 | 项目架构、DBUtil、InitDatabase、登录模块 | 框架搭建 + 登录功能 |
| 后端开发A | 1人 | 幼儿学籍 + 班级管理 + 调班管理 | Entity + DAO + Service + View |
| 后端开发B | 1人 | 课程管理 + 食谱管理 | Entity + DAO + Service + View |
| 后端开发C | 1人 | 考勤管理 + 数据统计 | Entity + DAO + Service + View |
| 文档/PPT | 1人 | 项目文档 + PPT + 测试 | 文档 + 演示材料 + 测试报告 |

> **注意：** 文档角色的同学也需要参与代码审查和集成测试，确保系统整体一致性。

## 7. 开发里程碑

| 阶段 | 时间 | 目标 |
|------|------|------|
| 第1阶段 | 第1~2天 | 搭建项目框架、数据库初始化、登录模块 |
| 第2阶段 | 第3~5天 | 幼儿学籍、班级管理、课程管理 |
| 第3阶段 | 第6~7天 | 食谱管理、调班管理 |
| 第4阶段 | 第8~9天 | 考勤管理、数据统计 |
| 第5阶段 | 第10天 | 集成测试、Bug修复 |
| 第6阶段 | 第11~12天 | 文档撰写、PPT制作、答辩准备 |

## 8. 连接配置说明

数据库连接参数集中在 `DBUtil.java` 中，部署时修改以下配置：

```java
// MySQL连接配置 — 部署时根据实际环境修改
private static final String URL      = "jdbc:mysql://localhost:3306/kindergarten?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
private static final String USERNAME = "root";
private static final String PASSWORD = "123456";  // 修改为实际密码
```

**环境要求：**
- MySQL 8.0+ 已安装并启动
- 已创建数据库用户或使用root
- MySQL Connector/J 8.0.x 驱动jar包已加入项目classpath
