package kindergarten;

import kindergarten.service.StatisticsService;
import kindergarten.util.InitDatabase;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * StatisticsService测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试数据统计业务逻辑
 */
public class StatisticsServiceTest {

    public static void main(String[] args) {
        System.out.println("══════ StatisticsService测试 ══════\n");
        new InitDatabase().init();

        StatisticsService service = new StatisticsService();
        int passed = 0, failed = 0;

        // 测试1：班级人数统计
        System.out.println("[测试1] 班级人数统计");
        List<Map<String, Object>> classStats = service.getClassStatistics();
        if (classStats.size() == 9) {
            System.out.printf("  ✓ PASS：共%d个班级\n", classStats.size());
            int total = 0;
            for (Map<String, Object> row : classStats) {
                total += (int) row.get("currentCount");
                System.out.printf("    %s：%d人（男%d 女%d）\n",
                    row.get("className"), row.get("currentCount"),
                    row.get("boyCount"), row.get("girlCount"));
            }
            System.out.printf("  总人数：%d\n", total);
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望9个班级，实际%d个\n", classStats.size());
            failed++;
        }

        // 测试2：课程选课统计
        System.out.println("\n[测试2] 课程选课统计");
        List<Map<String, Object>> courseStats = service.getCourseStatistics();
        if (courseStats.size() >= 4) {
            System.out.printf("  ✓ PASS：共%d门课程\n", courseStats.size());
            for (Map<String, Object> row : courseStats) {
                System.out.printf("    %s：%d/%d人（%s%%）\n",
                    row.get("courseName"), row.get("currentCount"),
                    row.get("maxCount"), row.get("rate"));
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望至少4门课程\n");
            failed++;
        }

        // 测试3：年级分布统计
        System.out.println("\n[测试3] 年级分布统计");
        List<Map<String, Object>> gradeStats = service.getGradeStatistics();
        if (gradeStats.size() == 3) {
            System.out.printf("  ✓ PASS：共%d个年级\n", gradeStats.size());
            for (Map<String, Object> row : gradeStats) {
                System.out.printf("    %s：%d人，%d个班，班均%s人\n",
                    row.get("grade"), row.get("totalStudents"),
                    row.get("classCount"), row.get("avgPerClass"));
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望3个年级，实际%d个\n", gradeStats.size());
            failed++;
        }

        // 测试4：出勤率报表
        System.out.println("\n[测试4] 出勤率报表");
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        List<Map<String, Object>> attStats = service.getAttendanceRateReport(start, end);
        if (attStats.size() == 9) {
            System.out.printf("  ✓ PASS：%d个班级的出勤率\n", attStats.size());
            for (Map<String, Object> row : attStats) {
                System.out.printf("    %s：出勤率%s%%（%d/%d条记录）\n",
                    row.get("className"), row.get("rate"),
                    row.get("present"), row.get("total"));
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望9个班级，实际%d个\n", attStats.size());
            failed++;
        }

        // 测试5：总人数统计
        System.out.println("\n[测试5] 总人数统计");
        int total = service.getTotalChildren();
        if (total == 90) {
            System.out.printf("  ✓ PASS：总人数%d\n", total);
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望90，实际%d\n", total);
            failed++;
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
