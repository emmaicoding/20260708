package kindergarten.util;

import java.sql.*;
import java.time.LocalDate;

/**
 * 数据库初始化类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 系统首次运行时自动创建数据库、建表、插入预置数据。
 *              使用CREATE IF NOT EXISTS确保可重复执行。
 */
public class InitDatabase {

    /**
     * 执行完整初始化流程
     */
    public void init() {
        System.out.println("[系统] 正在初始化数据库...");
        createDatabase();
        createTables();
        insertPresetData();
        System.out.println("[系统] 数据库初始化完成！");
    }

    /** 创建kindergarten数据库（如不存在） */
    private void createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS kindergarten DEFAULT CHARACTER SET utf8mb4";
        try (Connection conn = DBUtil.getBaseConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("  ✓ 数据库 kindergarten 已就绪");
        } catch (SQLException e) {
            System.out.println("  ✗ 创建数据库失败：" + e.getMessage());
            throw new RuntimeException("数据库创建失败", e);
        }
    }

    /** 创建所有数据表 */
    private void createTables() {
        String[] sqls = {
            // 班级表
            "CREATE TABLE IF NOT EXISTS t_class_info (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID'," +
            "  class_name VARCHAR(20) NOT NULL UNIQUE COMMENT '班级名称'," +
            "  grade VARCHAR(10) NOT NULL COMMENT '年级'," +
            "  max_count INT NOT NULL DEFAULT 10 COMMENT '最大人数'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '班级信息表'",

            // 用户表
            "CREATE TABLE IF NOT EXISTS t_user (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
            "  username VARCHAR(30) NOT NULL UNIQUE COMMENT '用户名'," +
            "  password VARCHAR(64) NOT NULL COMMENT '密码'," +
            "  real_name VARCHAR(30) NOT NULL COMMENT '真实姓名'," +
            "  role TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1管理员 2教师'," +
            "  class_id INT DEFAULT NULL COMMENT '教师负责班级'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (class_id) REFERENCES t_class_info(id)" +
            ") COMMENT '用户表'",

            // 幼儿表
            "CREATE TABLE IF NOT EXISTS t_child (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '幼儿ID'," +
            "  name VARCHAR(50) NOT NULL COMMENT '姓名'," +
            "  gender CHAR(1) NOT NULL COMMENT '性别 M/F'," +
            "  birth_date DATE NOT NULL COMMENT '出生日期'," +
            "  parent_name VARCHAR(50) NOT NULL COMMENT '家长姓名'," +
            "  parent_phone VARCHAR(20) NOT NULL COMMENT '家长电话'," +
            "  class_id INT NOT NULL COMMENT '所在班级'," +
            "  enrollment_date DATE NOT NULL COMMENT '入园日期'," +
            "  status TINYINT NOT NULL DEFAULT 1 COMMENT '1在园 0离园'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (class_id) REFERENCES t_class_info(id)" +
            ") COMMENT '幼儿信息表'",

            // 课程表
            "CREATE TABLE IF NOT EXISTS t_course (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID'," +
            "  course_name VARCHAR(50) NOT NULL UNIQUE COMMENT '课程名'," +
            "  max_count INT NOT NULL DEFAULT 15 COMMENT '容量上限'," +
            "  description VARCHAR(200) DEFAULT NULL COMMENT '课程描述'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '兴趣课程表'",

            // 选课关系表
            "CREATE TABLE IF NOT EXISTS t_child_course (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  course_id INT NOT NULL COMMENT '课程ID'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  UNIQUE KEY uk_child_course (child_id, course_id)," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE," +
            "  FOREIGN KEY (course_id) REFERENCES t_course(id)" +
            ") COMMENT '选课关系表'",

            // 菜品表
            "CREATE TABLE IF NOT EXISTS t_dish (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID'," +
            "  dish_name VARCHAR(50) NOT NULL COMMENT '菜品名'," +
            "  dish_type TINYINT NOT NULL COMMENT '1主食 2荤菜 3素菜 4汤 5水果'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '菜品库表'",

            // 每周食谱表
            "CREATE TABLE IF NOT EXISTS t_weekly_menu (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  week_start DATE NOT NULL COMMENT '周起始日期'," +
            "  day_of_week TINYINT NOT NULL COMMENT '1周一~5周五'," +
            "  meal_type TINYINT NOT NULL COMMENT '1早 2中 3晚'," +
            "  dish_id INT NOT NULL COMMENT '菜品ID'," +
            "  FOREIGN KEY (dish_id) REFERENCES t_dish(id)" +
            ") COMMENT '每周食谱表'",

            // 考勤表
            "CREATE TABLE IF NOT EXISTS t_attendance (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  attend_date DATE NOT NULL COMMENT '考勤日期'," +
            "  status TINYINT NOT NULL COMMENT '1出勤 2缺勤 3请假 4迟到'," +
            "  remark VARCHAR(100) DEFAULT NULL COMMENT '备注'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  UNIQUE KEY uk_child_date (child_id, attend_date)," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE" +
            ") COMMENT '考勤记录表'",

            // 调班记录表
            "CREATE TABLE IF NOT EXISTS t_transfer_log (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  old_class_id INT NOT NULL COMMENT '原班级'," +
            "  new_class_id INT NOT NULL COMMENT '新班级'," +
            "  operator_id INT NOT NULL COMMENT '操作人'," +
            "  transfer_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调班时间'," +
            "  remark VARCHAR(100) DEFAULT NULL COMMENT '备注'," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id)," +
            "  FOREIGN KEY (old_class_id) REFERENCES t_class_info(id)," +
            "  FOREIGN KEY (new_class_id) REFERENCES t_class_info(id)," +
            "  FOREIGN KEY (operator_id) REFERENCES t_user(id)" +
            ") COMMENT '调班记录表'"
        };

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.executeUpdate(sql);
            }
            System.out.println("  ✓ 数据表已就绪（共9张表）");
        } catch (SQLException e) {
            System.out.println("  ✗ 创建数据表失败：" + e.getMessage());
            throw new RuntimeException("建表失败", e);
        }
    }

    /** 插入预置数据（仅在表为空时插入） */
    private void insertPresetData() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // 检查是否已有数据
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_class_info");
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("  ✓ 预置数据已存在，跳过初始化");
                rs.close();
                return;
            }
            rs.close();

            // 1. 插入9个班级
            String[] classData = {
                "('大一班','大班',20)", "('大二班','大班',20)", "('大三班','大班',20)",
                "('中一班','中班',20)", "('中二班','中班',20)", "('中三班','中班',20)",
                "('小一班','小班',20)", "('小二班','小班',20)", "('小三班','小班',20)"
            };
            for (String data : classData) {
                stmt.executeUpdate("INSERT INTO t_class_info(class_name,grade,max_count) VALUES" + data);
            }
            System.out.println("  ✓ 班级数据已插入（9个班级）");

            // 2. 插入管理员账号（id=1，密码SHA-256哈希存储）
            String adminHash = PasswordUtil.hash("admin123");
            stmt.executeUpdate("INSERT INTO t_user(username,password,real_name,role,class_id) " +
                "VALUES('admin','" + adminHash + "','系统管理员',1,NULL)");
            // 3. 插入9个教师账号（密码SHA-256哈希存储）
            String teacherHash = PasswordUtil.hash("123456");
            for (int i = 1; i <= 9; i++) {
                stmt.executeUpdate(String.format(
                    "INSERT INTO t_user(username,password,real_name,role,class_id) " +
                    "VALUES('teacher%02d','%s','%s教师',2,%d)",
                    i, teacherHash, getClassName(i), i));
            }
            System.out.println("  ✓ 用户账号已插入（1管理员+9教师）");

            // 4. 插入4门兴趣课程
            stmt.executeUpdate("INSERT INTO t_course(course_name,max_count,description) VALUES('舞蹈',100,'培养幼儿舞蹈兴趣和形体美感')");
            stmt.executeUpdate("INSERT INTO t_course(course_name,max_count,description) VALUES('跆拳道',100,'强身健体，培养纪律意识')");
            stmt.executeUpdate("INSERT INTO t_course(course_name,max_count,description) VALUES('钢琴',100,'音乐启蒙，培养艺术修养')");
            stmt.executeUpdate("INSERT INTO t_course(course_name,max_count,description) VALUES('美术',100,'培养色彩感知和创造力')");
            System.out.println("  ✓ 兴趣课程已插入（4门）");

            // 5. 插入菜品库（20个）
            String[] dishes = {
                "('白米饭',1)", "('小米粥',1)", "('馒头',1)", "('面条',1)",
                "('红烧排骨',2)", "('糖醋里脊',2)", "('番茄炒蛋',2)", "('可乐鸡翅',2)",
                "('清炒西兰花',3)", "('白菜炖豆腐',3)", "('土豆丝',3)", "('胡萝卜炒肉',3)",
                "('紫菜蛋花汤',4)", "('西红柿蛋汤',4)", "('玉米排骨汤',4)", "('冬瓜汤',4)",
                "('苹果',5)", "('香蕉',5)", "('橘子',5)", "('西瓜',5)"
            };
            for (String d : dishes) {
                stmt.executeUpdate("INSERT INTO t_dish(dish_name,dish_type) VALUES" + d);
            }
            System.out.println("  ✓ 菜品库已插入（20道菜品）");

            // 6. 插入90个幼儿（每班10人）
            String[] boyNames = {
                "张小明","李小强","王小华","刘小杰","陈小宇",
                "杨小磊","赵小军","周小伟","吴小鹏","孙小浩",
                "马天宇","林子轩","黄志远","何建国","郑凯文",
                "罗俊杰","谢明辉","唐浩然","韩志豪","曹文博",
                "邓伟明","萧国庆","龙飞鸿","万嘉伟","段鹏程",
                "雷震宇","钱多多","尹志平","孔令辉","白敬亭",
                "石家豪","崔大伟","潘明轩","秦少华","尤浩然",
                "许文龙","龚子昂","严浩翔","贺子龙","向天歌",
                "聂风华","廖俊逸","贾宝玉","庞大海","樊少皇"
            };
            String[] girlNames = {
                "李小红","王小芳","张小丽","刘小美","陈小雪",
                "杨小玲","赵小燕","周小莉","吴小娟","孙小婷",
                "马思纯","林心如","黄诗琪","何芷若","郑秀晶",
                "罗玉凤","谢雨欣","唐嫣然","韩冰冰","曹颖慧",
                "邓紫棋","萧亚轩","龙婉清","万绮雯","段思思",
                "雷佳音","钱佩玲","尹甜甜","孔雪儿","白雪晴",
                "石雪兰","崔文静","潘晓婷","秦海璐","尤小茹",
                "许晴儿","龚琳娜","严莉莉","贺子枫","向语嫣",
                "聂小倩","廖碧儿","贾静雯","庞晓燕","樊梨花"
            };
            String[] parentSuffix = {"爸爸","妈妈"};

            int childId = 1;
            int boyIndex = 0;
            int girlIndex = 0;
            String childSql = "INSERT INTO t_child(name,gender,birth_date,parent_name,parent_phone,class_id,enrollment_date,status) " +
                              "VALUES(?,?,?,?,?,?,?,1)";
            try (PreparedStatement psChild = conn.prepareStatement(childSql)) {
                for (int classId = 1; classId <= 9; classId++) {
                    // 根据班级确定出生年份：大班2020、中班2021、小班2022
                    int birthYear;
                    if (classId <= 3) birthYear = 2020;      // 大班（5~6岁）
                    else if (classId <= 6) birthYear = 2021;  // 中班（4~5岁）
                    else birthYear = 2022;                    // 小班（3~4岁）

                    for (int i = 0; i < 10; i++) {
                        boolean isBoy = i < 5;
                        String name = isBoy ? boyNames[boyIndex++] : girlNames[girlIndex++];
                        String gender = isBoy ? "M" : "F";
                        String parentName = name.substring(0, 3) + parentSuffix[isBoy ? 0 : 1];
                        String phone = String.format("138%08d", 10000000 + classId * 100 + i);
                        int month = 1 + (childId % 12);
                        int day = 1 + (childId % 28);
                        String birthDate = String.format("%d-%02d-%02d", birthYear, month, day);

                        psChild.setString(1, name);
                        psChild.setString(2, gender);
                        psChild.setString(3, birthDate);
                        psChild.setString(4, parentName);
                        psChild.setString(5, phone);
                        psChild.setInt(6, classId);
                        psChild.setString(7, "2025-09-01");
                        psChild.executeUpdate();
                        childId++;
                    }
                }
            }
            System.out.println("  ✓ 幼儿数据已插入（90名幼儿）");

            // 7. 为每个幼儿随机分配2~4门课程（体现选课差异性）
            String ccSql = "INSERT INTO t_child_course(child_id,course_id) VALUES(?,?)";
            try (PreparedStatement psCC = conn.prepareStatement(ccSql)) {
                for (int cid = 1; cid <= 90; cid++) {
                    // 根据childId确定选课数量和组合
                    int courseCount = 2 + (cid % 3); // 2、3、4门轮替
                    // 根据childId确定选哪些课（产生差异化分布）
                    for (int j = 0; j < courseCount; j++) {
                        int courseId = 1 + ((cid + j * 3) % 4); // 课程1~4交叉分配
                        psCC.setInt(1, cid);
                        psCC.setInt(2, courseId);
                        psCC.executeUpdate();
                    }
                }
            }
            System.out.println("  ✓ 选课数据已插入（每人2~4门，差异化分配）");

            // 8. 插入本周食谱（周一~周五，合理的早午晚餐搭配）
            // 菜品ID：1~4主食, 5~8荤菜, 9~12素菜, 13~16汤, 17~20水果
            LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            String menuSql = "INSERT INTO t_weekly_menu(week_start,day_of_week,meal_type,dish_id) VALUES(?,?,?,?)";
            try (PreparedStatement psMenu = conn.prepareStatement(menuSql)) {
                for (int day = 1; day <= 5; day++) {
                    // 早餐：主食1道 + 水果1道
                    psMenu.setString(1, monday.toString());
                    psMenu.setInt(2, day);
                    psMenu.setInt(3, 1); // 早餐
                    psMenu.setInt(4, ((day - 1) % 4) + 1); // 主食1~4轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 1); // 早餐
                    psMenu.setInt(4, ((day - 1) % 4) + 17); // 水果17~20轮替
                    psMenu.executeUpdate();

                    // 午餐：主食1道 + 荤菜1道 + 素菜1道 + 汤1道
                    psMenu.setInt(3, 2); // 午餐
                    psMenu.setInt(4, ((day + 1) % 4) + 1); // 主食轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 2);
                    psMenu.setInt(4, ((day - 1) % 4) + 5); // 荤菜5~8轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 2);
                    psMenu.setInt(4, ((day - 1) % 4) + 9); // 素菜9~12轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 2);
                    psMenu.setInt(4, ((day - 1) % 4) + 13); // 汤13~16轮替
                    psMenu.executeUpdate();

                    // 晚餐：主食1道 + 素菜1道 + 汤1道
                    psMenu.setInt(3, 3); // 晚餐
                    psMenu.setInt(4, ((day + 2) % 4) + 1); // 主食轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 3);
                    psMenu.setInt(4, ((day + 1) % 4) + 9); // 素菜轮替
                    psMenu.executeUpdate();

                    psMenu.setInt(3, 3);
                    psMenu.setInt(4, ((day + 1) % 4) + 13); // 汤轮替
                    psMenu.executeUpdate();
                }
            }
            System.out.println("  ✓ 本周食谱已插入（早餐主食+水果，午餐主食+荤菜+素菜+汤，晚餐主食+素菜+汤）");

            // 9. 插入最近5天考勤（模拟数据）
            LocalDate today = LocalDate.now();
            for (int d = 0; d < 5; d++) {
                LocalDate date = today.minusDays(d);
                // 跳过周末
                if (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) continue;
                for (int cid = 1; cid <= 90; cid++) {
                    // 90%出勤，5%缺勤，3%请假，2%迟到
                    int status;
                    int rand = cid % 100;
                    if (rand < 90) status = 1;
                    else if (rand < 95) status = 2;
                    else if (rand < 98) status = 3;
                    else status = 4;

                    stmt.executeUpdate(String.format(
                        "INSERT INTO t_attendance(child_id,attend_date,status) VALUES(%d,'%s',%d)",
                        cid, date, status));
                }
            }
            System.out.println("  ✓ 考勤样本数据已插入");

            // 10. 插入2条调班记录
            stmt.executeUpdate("INSERT INTO t_transfer_log(child_id,old_class_id,new_class_id,operator_id,remark) " +
                "VALUES(1,1,4,1,'测试调班：大一班调至中一班')");
            stmt.executeUpdate("INSERT INTO t_transfer_log(child_id,old_class_id,new_class_id,operator_id,remark) " +
                "VALUES(11,2,5,1,'测试调班：大二班调至中二班')");
            System.out.println("  ✓ 调班记录已插入（2条测试数据）");

        } catch (SQLException e) {
            System.out.println("  ✗ 插入预置数据失败：" + e.getMessage());
            throw new RuntimeException("插入预置数据失败", e);
        }
    }

    /** 根据班级ID获取班级名称 */
    private String getClassName(int classId) {
        String[] names = {"大一班","大二班","大三班","中一班","中二班","中三班","小一班","小二班","小三班"};
        return names[classId - 1];
    }
}
