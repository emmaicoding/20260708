package kindergarten;

import kindergarten.util.DBUtil;
import kindergarten.util.InitDatabase;

import java.sql.*;

/**
 * 数据库初始化测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试数据库初始化是否成功，验证表结构和预置数据
 */
public class InitDatabaseTest {

    public static void main(String[] args) {
        System.out.println("══════ 数据库初始化测试 ══════\n");
        int passed = 0, failed = 0;

        // 执行初始化
        new InitDatabase().init();

        // 测试1：数据库连接
        System.out.println("\n[测试1] 数据库连接");
        if (DBUtil.testConnection()) {
            System.out.println("  ✓ PASS：数据库连接成功");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：数据库连接失败");
            failed++;
        }

        // 测试2：表是否存在
        System.out.println("\n[测试2] 数据表创建");
        String[] tables = {"t_user", "t_class_info", "t_child", "t_course",
                           "t_child_course", "t_dish", "t_weekly_menu",
                           "t_attendance", "t_transfer_log"};
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    rs.next();
                    System.out.printf("  ✓ 表 %s 存在，%d条记录\n", table, rs.getInt(1));
                }
            }
            System.out.println("  ✓ PASS：所有数据表已创建");
            passed++;
        } catch (SQLException e) {
            System.out.println("  ✗ FAIL：" + e.getMessage());
            failed++;
        }

        // 测试3：预置数据验证
        System.out.println("\n[测试3] 预置数据验证");
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // 班级数量
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_class_info")) {
                rs.next();
                int classCount = rs.getInt(1);
                System.out.printf("  班级数量：%d（期望：9）%s\n", classCount, classCount == 9 ? "✓" : "✗");
                if (classCount == 9) passed++; else failed++;
            }

            // 幼儿数量
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_child WHERE status = 1")) {
                rs.next();
                int childCount = rs.getInt(1);
                System.out.printf("  幼儿数量：%d（期望：90）%s\n", childCount, childCount == 90 ? "✓" : "✗");
                if (childCount == 90) passed++; else failed++;
            }

            // 课程数量
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_course")) {
                rs.next();
                int courseCount = rs.getInt(1);
                System.out.printf("  课程数量：%d（期望：4）%s\n", courseCount, courseCount == 4 ? "✓" : "✗");
                if (courseCount == 4) passed++; else failed++;
            }

            // 选课记录
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_child_course")) {
                rs.next();
                int ccCount = rs.getInt(1);
                System.out.printf("  选课记录：%d（期望：360）%s\n", ccCount, ccCount == 360 ? "✓" : "✗");
                if (ccCount == 360) passed++; else failed++;
            }

            // 用户数量
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_user")) {
                rs.next();
                int userCount = rs.getInt(1);
                System.out.printf("  用户数量：%d（期望：10）%s\n", userCount, userCount == 10 ? "✓" : "✗");
                if (userCount == 10) passed++; else failed++;
            }
        } catch (SQLException e) {
            System.out.println("  ✗ FAIL：" + e.getMessage());
            failed++;
        }

        // 汇总
        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
