package kindergarten.view;

import kindergarten.entity.User;
import kindergarten.entity.Child;
import kindergarten.entity.Attendance;
import kindergarten.entity.WeeklyMenu;
import kindergarten.service.ChildService;
import kindergarten.service.AttendanceService;
import kindergarten.service.MenuService;
import kindergarten.service.CourseService;
import kindergarten.entity.ChildCourse;
import kindergarten.util.InputUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 教师功能菜单视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 教师角色的功能界面，仅可查看本班数据和记录考勤
 */
public class TeacherView {
    private final ChildService childService = new ChildService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final MenuService menuService = new MenuService();
    private final CourseService courseService = new CourseService();
    private final LoginView loginView = new LoginView();

    /**
     * 显示教师功能菜单
     *
     * @param user 当前登录教师
     */
    public void show(User user) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          教师功能菜单                      ║");
            System.out.println("║  欢迎：" + padRight(user.getRealName(), 30) + "   ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. 查看本班幼儿                          ║");
            System.out.println("║  2. 考勤打卡                              ║");
            System.out.println("║  3. 查看本班考勤记录                       ║");
            System.out.println("║  4. 查看课程安排                          ║");
            System.out.println("║  5. 查看本周食谱                          ║");
            System.out.println("║  6. 修改密码                              ║");
            System.out.println("║  0. 退出登录                              ║");
            System.out.println("╚══════════════════════════════════════════╝");

            int choice = InputUtil.readInt("请选择功能编号：", 0, 6);
            switch (choice) {
                case 1: showMyClass(user); break;
                case 2: doAttendance(user); break;
                case 3: showAttendance(user); break;
                case 4: showCourses(user); break;
                case 5: showMenu(); break;
                case 6: loginView.showChangePassword(user); break;
                case 0: return;
            }
        }
    }

    /** 查看本班幼儿 */
    private void showMyClass(User user) {
        if (user.getClassId() == null) {
            System.out.println("  ✗ 您未分配班级");
            return;
        }
        List<Child> children = childService.getChildrenByClass(user.getClassId());
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  %s 幼儿名单（共%d人）\n", user.getClassName(), children.size());
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %-6s %-10s %-6s %-14s %-10s %-15s\n",
            "编号", "姓名", "性别", "出生日期", "家长", "联系电话");
        System.out.println("──────────────────────────────────────────────────────");
        for (Child c : children) {
            System.out.printf("  %-6d %-10s %-6s %-14s %-10s %-15s\n",
                c.getId(), c.getName(), c.getGenderName(),
                c.getBirthDate(), c.getParentName(), c.getParentPhone());
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 考勤打卡 */
    private void doAttendance(User user) {
        if (user.getClassId() == null) {
            System.out.println("  ✗ 您未分配班级");
            return;
        }
        LocalDate today = LocalDate.now();
        System.out.println("\n══════ 考勤打卡（" + user.getClassName() + " " + today + "）══════");
        System.out.println("  状态：1=出勤  2=缺勤  3=请假  4=迟到");

        List<Child> children = childService.getChildrenByClass(user.getClassId());
        // 先查看今日是否已有记录
        List<Attendance> existing = attendanceService.getClassAttendance(user.getClassId(), today);
        if (!existing.isEmpty()) {
            System.out.println("  ⚠ 今日已有考勤记录，重新打卡将覆盖原有记录");
            if (!InputUtil.readConfirm("  是否继续？")) return;
        }

        // 全班统一模式
        System.out.println("\n  打卡模式：");
        System.out.println("  1. 全班统一状态");
        System.out.println("  2. 逐个打卡");
        int mode = InputUtil.readInt("请选择：", 1, 2);

        if (mode == 1) {
            int status = InputUtil.readInt("  请输入全班状态（1出勤/2缺勤/3请假/4迟到）：", 1, 4);
            int count = attendanceService.batchRecordByClass(user.getClassId(), today, status);
            System.out.println("  ✓ 已记录" + count + "人考勤（" + attendanceService.getStatusName(status) + "）");
        } else {
            for (Child child : children) {
                System.out.printf("  %s（ID:%d）状态：", child.getName(), child.getId());
                int status = InputUtil.readInt("", 1, 4);
                attendanceService.recordAttendance(child.getId(), today, status, null);
            }
            System.out.println("  ✓ 全班考勤记录完成");
        }
        InputUtil.waitForEnter();
    }

    /** 查看本班考勤记录 */
    private void showAttendance(User user) {
        if (user.getClassId() == null) {
            System.out.println("  ✗ 您未分配班级");
            return;
        }
        LocalDate date = InputUtil.readDate("  请输入查询日期");
        List<Attendance> records = attendanceService.getClassAttendance(user.getClassId(), date);
        if (records.isEmpty()) {
            System.out.println("  暂无考勤记录");
            InputUtil.waitForEnter();
            return;
        }
        System.out.println("\n══════ " + user.getClassName() + " " + date + " 考勤记录 ══════");
        System.out.printf("  %-6s %-10s %-8s %-10s\n", "编号", "姓名", "状态", "备注");
        System.out.println("────────────────────────────────────────────");
        int present = 0, absent = 0, leave = 0, late = 0;
        for (Attendance a : records) {
            System.out.printf("  %-6d %-10s %-8s %-10s\n",
                a.getChildId(), a.getChildName(), a.getStatusName(),
                a.getRemark() != null ? a.getRemark() : "");
            switch (a.getStatus()) {
                case 1: present++; break;
                case 2: absent++; break;
                case 3: leave++; break;
                case 4: late++; break;
            }
        }
        System.out.println("────────────────────────────────────────────");
        System.out.printf("  出勤：%d  缺勤：%d  请假：%d  迟到：%d\n", present, absent, leave, late);
        InputUtil.waitForEnter();
    }

    /** 查看课程安排 */
    private void showCourses(User user) {
        if (user.getClassId() == null) {
            System.out.println("  ✗ 您未分配班级");
            return;
        }
        System.out.println("\n══════ " + user.getClassName() + " 课程安排 ══════");
        List<Child> children = childService.getChildrenByClass(user.getClassId());
        for (Child child : children) {
            List<ChildCourse> courses = courseService.getChildCourses(child.getId());
            StringBuilder sb = new StringBuilder();
            for (ChildCourse cc : courses) {
                if (sb.length() > 0) sb.append("、");
                sb.append(cc.getCourseName());
            }
            System.out.printf("  %s：%s\n", child.getName(),
                sb.length() > 0 ? sb.toString() : "未选课");
        }
        System.out.println("════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 查看本周食谱 */
    private void showMenu() {
        List<WeeklyMenu> menus = menuService.getCurrentWeekMenu();
        if (menus.isEmpty()) {
            System.out.println("  本周暂未安排食谱");
            InputUtil.waitForEnter();
            return;
        }
        System.out.println("\n══════ 本周食谱 ══════");
        String[] dayNames = {"", "周一", "周二", "周三", "周四", "周五"};
        for (int day = 1; day <= 5; day++) {
            System.out.println("\n  ── " + dayNames[day] + " ──");
            for (WeeklyMenu m : menus) {
                if (m.getDayOfWeek() == day) {
                    System.out.printf("    %s：%s\n", m.getMealName(), m.getDishName());
                }
            }
        }
        System.out.println("\n════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 右填充空格 */
    private String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(' ');
        return sb.toString();
    }
}
