package kindergarten.view;

import kindergarten.entity.Attendance;
import kindergarten.entity.ClassInfo;
import kindergarten.entity.Child;
import kindergarten.service.AttendanceService;
import kindergarten.service.ChildService;
import kindergarten.dao.ClassDao;
import kindergarten.util.InputUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，支持考勤记录查看和出勤率统计
 */
public class AttendanceView {
    private final AttendanceService attendanceService = new AttendanceService();
    private final ChildService childService = new ChildService();
    private final ClassDao classDao = new ClassDao();

    /**
     * 显示考勤管理菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n══════ 考勤管理 ══════");
            System.out.println("  1. 查看班级今日考勤");
            System.out.println("  2. 查看班级指定日期考勤");
            System.out.println("  3. 查看幼儿考勤记录");
            System.out.println("  4. 统计班级出勤率");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 4);
            switch (choice) {
                case 1: showTodayAttendance(); break;
                case 2: showDateAttendance(); break;
                case 3: showChildAttendance(); break;
                case 4: showAttendanceRate(); break;
                case 0: return;
            }
        }
    }

    /** 查看班级今日考勤 */
    private void showTodayAttendance() {
        int classId = selectClass();
        if (classId <= 0) return;
        showAttendanceByDate(classId, LocalDate.now());
    }

    /** 查看班级指定日期考勤 */
    private void showDateAttendance() {
        int classId = selectClass();
        if (classId <= 0) return;
        LocalDate date = InputUtil.readDate("  请输入查询日期");
        showAttendanceByDate(classId, date);
    }

    /** 显示考勤详情 */
    private void showAttendanceByDate(int classId, LocalDate date) {
        ClassInfo cls = classDao.selectById(classId);
        List<Attendance> records = attendanceService.getClassAttendance(classId, date);
        if (records.isEmpty()) {
            System.out.println("  暂无考勤记录");
            InputUtil.waitForEnter();
            return;
        }
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  %s %s 考勤记录\n", cls != null ? cls.getClassName() : "", date);
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s%s\n",
            InputUtil.padRight("幼儿ID", 10), InputUtil.padRight("姓名", 12),
            InputUtil.padRight("状态", 10), InputUtil.padRight("备注", 12));
        System.out.println("──────────────────────────────────────────────────────");
        int[] counts = new int[5];
        for (Attendance a : records) {
            System.out.printf("  %s%s%s%s\n",
                InputUtil.padRight(String.valueOf(a.getChildId()), 10),
                InputUtil.padRight(a.getChildName(), 12),
                InputUtil.padRight(a.getStatusName(), 10),
                InputUtil.padRight(a.getRemark() != null ? a.getRemark() : "", 12));
            counts[a.getStatus()]++;
        }
        System.out.println("──────────────────────────────────────────────────────");
        System.out.printf("  出勤：%d  缺勤：%d  请假：%d  迟到：%d  合计：%d\n",
            counts[1], counts[2], counts[3], counts[4], records.size());
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 查看幼儿考勤记录 */
    private void showChildAttendance() {
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        Child child = childService.getChildById(childId);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        LocalDate start = InputUtil.readDate("  开始日期");
        LocalDate end = InputUtil.readDate("  结束日期");
        List<Attendance> records = attendanceService.getChildAttendance(childId, start, end);
        if (records.isEmpty()) {
            System.out.println("  暂无考勤记录");
            InputUtil.waitForEnter();
            return;
        }
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  %s（%s）考勤记录 %s ~ %s\n",
            child.getName(), child.getClassName(), start, end);
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s\n",
            InputUtil.padRight("日期", 16), InputUtil.padRight("状态", 10), InputUtil.padRight("备注", 12));
        System.out.println("──────────────────────────────────────────────────────");
        for (Attendance a : records) {
            System.out.printf("  %s%s%s\n",
                InputUtil.padRight(String.valueOf(a.getAttendDate()), 16),
                InputUtil.padRight(a.getStatusName(), 10),
                InputUtil.padRight(a.getRemark() != null ? a.getRemark() : "", 12));
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 统计班级出勤率 */
    private void showAttendanceRate() {
        int classId = selectClass();
        if (classId <= 0) return;
        LocalDate start = InputUtil.readDate("  开始日期");
        LocalDate end = InputUtil.readDate("  结束日期");
        int[] stats = attendanceService.getClassAttendanceStats(classId, start, end);
        double rate = attendanceService.calcAttendanceRate(stats);

        ClassInfo cls = classDao.selectById(classId);
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  %s 出勤率统计（%s ~ %s）\n",
            cls != null ? cls.getClassName() : "", start, end);
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  总记录数：%d\n", stats[0]);
        System.out.printf("  出勤：%d\n", stats[1]);
        System.out.printf("  缺勤：%d\n", stats[2]);
        System.out.printf("  请假：%d\n", stats[3]);
        System.out.printf("  迟到：%d\n", stats[4]);
        System.out.printf("  出勤率：%.1f%%\n", rate);
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 选择班级（公共方法） */
    private int selectClass() {
        List<ClassInfo> classes = classDao.selectAll();
        System.out.println("  可选班级：");
        for (ClassInfo cls : classes) {
            System.out.printf("    %d. %s（%s）\n", cls.getId(), cls.getClassName(), cls.getGrade());
        }
        return InputUtil.readInt("  请选择班级ID（0取消）：", 0, 9);
    }
}
