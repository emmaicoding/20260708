package kindergarten;

import kindergarten.entity.Attendance;
import kindergarten.service.AttendanceService;
import kindergarten.util.InitDatabase;

import java.time.LocalDate;
import java.util.List;

/**
 * AttendanceService测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试考勤记录、查询、统计等业务逻辑
 */
public class AttendanceServiceTest {

    public static void main(String[] args) {
        System.out.println("══════ AttendanceService测试 ══════\n");
        new InitDatabase().init();

        AttendanceService service = new AttendanceService();
        int passed = 0, failed = 0;
        LocalDate today = LocalDate.now();

        // 测试1：记录单个幼儿考勤
        System.out.println("[测试1] 记录单个幼儿考勤");
        String result = service.recordAttendance(1, today, 1, "测试出勤");
        if (result.contains("已记录") || result.contains("成功")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        // 测试2：无效状态校验
        System.out.println("[测试2] 无效状态校验");
        result = service.recordAttendance(1, today, 5, null);
        if (result.contains("无效")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示状态无效");
            failed++;
        }

        // 测试3：无效幼儿校验
        System.out.println("[测试3] 无效幼儿校验");
        result = service.recordAttendance(9999, today, 1, null);
        if (result.contains("不存在")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示幼儿不存在");
            failed++;
        }

        // 测试4：批量考勤
        System.out.println("[测试4] 批量班级考勤");
        int count = service.batchRecordByClass(1, today, 1);
        if (count == 10) {
            System.out.printf("  ✓ PASS：批量记录%d人\n", count);
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望10人，实际%d人\n", count);
            failed++;
        }

        // 测试5：查询班级当日考勤
        System.out.println("[测试5] 查询班级当日考勤");
        List<Attendance> records = service.getClassAttendance(1, today);
        if (records.size() == 10) {
            System.out.printf("  ✓ PASS：查询到%d条记录\n", records.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望10条，实际%d条\n", records.size());
            failed++;
        }

        // 测试6：查询幼儿历史考勤
        System.out.println("[测试6] 查询幼儿历史考勤");
        LocalDate start = today.minusDays(7);
        List<Attendance> history = service.getChildAttendance(1, start, today);
        if (!history.isEmpty()) {
            System.out.printf("  ✓ PASS：查询到%d条历史记录\n", history.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：历史记录为空");
            failed++;
        }

        // 测试7：统计出勤率
        System.out.println("[测试7] 统计班级出勤率");
        int[] stats = service.getClassAttendanceStats(1, start, today);
        double rate = service.calcAttendanceRate(stats);
        if (stats[0] > 0 && rate > 0) {
            System.out.printf("  ✓ PASS：总记录%d，出勤率%.1f%%\n", stats[0], rate);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：统计数据异常");
            failed++;
        }

        // 测试8：出勤率计算
        System.out.println("[测试8] 出勤率计算正确性");
        int[] testStats = {100, 90, 5, 3, 2}; // 总100，出勤90，缺勤5，请假3，迟到2
        double testRate = service.calcAttendanceRate(testStats);
        if (Math.abs(testRate - 92.0) < 0.1) {
            System.out.printf("  ✓ PASS：出勤率=%.1f%%（期望92.0%%）\n", testRate);
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：出勤率=%.1f%%（期望92.0%%）\n", testRate);
            failed++;
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
