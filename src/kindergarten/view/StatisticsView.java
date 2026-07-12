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
        System.out.printf("  %s%s%s%s%s%s\n",
            InputUtil.padRight("班级", 14), InputUtil.padRight("年级", 10), InputUtil.padRight("当前人数", 12),
            InputUtil.padRight("男生", 10), InputUtil.padRight("女生", 10), InputUtil.padRight("容量", 10));
        System.out.println("──────────────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            int count = (int) row.get("currentCount");
            int boys = (int) row.get("boyCount");
            int girls = (int) row.get("girlCount");
            int max = (int) row.get("maxCount");
            totalStudents += count;
            totalBoys += boys;
            totalGirls += girls;

            System.out.printf("  %s%s%s%s%s%s\n",
                InputUtil.padRight((String) row.get("className"), 14),
                InputUtil.padRight((String) row.get("grade"), 10),
                InputUtil.padRight(String.valueOf(count), 12),
                InputUtil.padRight(String.valueOf(boys), 10),
                InputUtil.padRight(String.valueOf(girls), 10),
                InputUtil.padRight(String.valueOf(max), 10));
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
        System.out.printf("  %s%s%s%s\n",
            InputUtil.padRight("课程名称", 14), InputUtil.padRight("已选人数", 12),
            InputUtil.padRight("容量上限", 12), InputUtil.padRight("选课率", 12));
        System.out.println("──────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %s%s%s%s\n",
                InputUtil.padRight((String) row.get("courseName"), 14),
                InputUtil.padRight(String.valueOf(row.get("currentCount")), 12),
                InputUtil.padRight(String.valueOf(row.get("maxCount")), 12),
                InputUtil.padRight(row.get("rate") + "%", 12));
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
        System.out.printf("  %s%s%s%s\n",
            InputUtil.padRight("年级", 14), InputUtil.padRight("总人数", 12),
            InputUtil.padRight("班级数", 12), InputUtil.padRight("班均人数", 12));
        System.out.println("──────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %s%s%s%s\n",
                InputUtil.padRight((String) row.get("grade"), 14),
                InputUtil.padRight(String.valueOf(row.get("totalStudents")), 12),
                InputUtil.padRight(String.valueOf(row.get("classCount")), 12),
                InputUtil.padRight(String.valueOf(row.get("avgPerClass")), 12));
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
        System.out.printf("  %s%s%s%s%s%s%s\n",
            InputUtil.padRight("班级", 14), InputUtil.padRight("总记录", 12), InputUtil.padRight("出勤", 12),
            InputUtil.padRight("缺勤", 12), InputUtil.padRight("请假", 12), InputUtil.padRight("迟到", 12), InputUtil.padRight("出勤率", 12));
        System.out.println("──────────────────────────────────────────────────────────────────────────");

        for (Map<String, Object> row : stats) {
            System.out.printf("  %s%s%s%s%s%s%s\n",
                InputUtil.padRight((String) row.get("className"), 14),
                InputUtil.padRight(String.valueOf(row.get("total")), 12),
                InputUtil.padRight(String.valueOf(row.get("present")), 12),
                InputUtil.padRight(String.valueOf(row.get("absent")), 12),
                InputUtil.padRight(String.valueOf(row.get("leave")), 12),
                InputUtil.padRight(String.valueOf(row.get("late")), 12),
                InputUtil.padRight(row.get("rate") + "%", 12));
        }
        System.out.println("══════════════════════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }
}
