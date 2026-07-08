package kindergarten.view;

import kindergarten.service.StatisticsService;
import kindergarten.util.InputUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据统计视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，展示各类数据统计报表
 */
public class StatisticsView {
    private final StatisticsService statisticsService = new StatisticsService();

    /**
     * 显示数据统计菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n══════ 数据统计 ══════");
            System.out.println("  1. 班级人数统计");
            System.out.println("  2. 课程选课统计");
            System.out.println("  3. 年级分布统计");
            System.out.println("  4. 出勤率报表");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 4);
            switch (choice) {
                case 1: showClassStats(); break;
                case 2: showCourseStats(); break;
                case 3: showGradeStats(); break;
                case 4: showAttendanceRate(); break;
                case 0: return;
            }
        }
    }

    /** 班级人数统计 */
    private void showClassStats() {
        List<Map<String, Object>> stats = statisticsService.getClassStatistics();
        int totalStudents = 0, totalBoys = 0, totalGirls = 0;

        System.out.println("\n══════════════════════════════════════════════════════════════");
        System.out.println("  班级人数统计");
        System.out.println("══════════════════════════════════════════════════════════════");
        System.out.printf("  %-10s %-8s %-10s %-8s %-8s %-8s\n",
            "班级", "年级", "当前人数", "男生", "女生", "容量");
        System.out.println("──────────────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            int count = (int) row.get("currentCount");
            int boys = (int) row.get("boyCount");
            int girls = (int) row.get("girlCount");
            int max = (int) row.get("maxCount");
            totalStudents += count;
            totalBoys += boys;
            totalGirls += girls;

            System.out.printf("  %-10s %-8s %-10d %-8d %-8d %-8d\n",
                row.get("className"), row.get("grade"), count, boys, girls, max);
        }

        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.printf("  合计：%d人（男生%d 女生%d）\n", totalStudents, totalBoys, totalGirls);
        System.out.println("══════════════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 课程选课统计 */
    private void showCourseStats() {
        List<Map<String, Object>> stats = statisticsService.getCourseStatistics();

        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  课程选课统计");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %-12s %-10s %-10s %-10s\n", "课程名称", "已选人数", "容量上限", "选课率");
        System.out.println("──────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %-12s %-10d %-10d %-10s\n",
                row.get("courseName"), row.get("currentCount"), row.get("maxCount"),
                row.get("rate") + "%");
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 年级分布统计 */
    private void showGradeStats() {
        List<Map<String, Object>> stats = statisticsService.getGradeStatistics();

        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  年级分布统计");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %-10s %-12s %-10s %-12s\n", "年级", "总人数", "班级数", "班均人数");
        System.out.println("──────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %-10s %-12d %-10d %-12s\n",
                row.get("grade"), row.get("totalStudents"), row.get("classCount"),
                row.get("avgPerClass"));
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 出勤率报表 */
    private void showAttendanceRate() {
        LocalDate start = InputUtil.readDate("  开始日期");
        LocalDate end = InputUtil.readDate("  结束日期");
        List<Map<String, Object>> stats = statisticsService.getAttendanceRateReport(start, end);

        System.out.println("\n══════════════════════════════════════════════════════════════════════");
        System.out.printf("  出勤率报表（%s ~ %s）\n", start, end);
        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.printf("  %-10s %-10s %-10s %-10s %-10s %-10s %-10s\n",
            "班级", "总记录", "出勤", "缺勤", "请假", "迟到", "出勤率");
        System.out.println("──────────────────────────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %-10s %-10d %-10d %-10d %-10d %-10d %-10s\n",
                row.get("className"), row.get("total"), row.get("present"),
                row.get("absent"), row.get("leave"), row.get("late"),
                row.get("rate") + "%");
        }
        System.out.println("══════════════════════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }
}
