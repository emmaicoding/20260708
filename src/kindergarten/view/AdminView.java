package kindergarten.view;

import kindergarten.entity.User;
import kindergarten.util.InputUtil;

/**
 * 管理员主菜单视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员功能菜单路由，可访问所有系统功能
 */
public class AdminView {
    private final ChildView childView = new ChildView();
    private final CourseView courseView = new CourseView();
    private final MenuView menuView = new MenuView();
    private final TransferView transferView = new TransferView();
    private final AttendanceView attendanceView = new AttendanceView();
    private final StatisticsView statisticsView = new StatisticsView();
    private final LoginView loginView = new LoginView();

    /**
     * 显示管理员功能菜单
     *
     * @param user 当前登录用户
     */
    public void show(User user) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          管理员功能菜单                    ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. 幼儿学籍管理                          ║");
            System.out.println("║  2. 课程管理                              ║");
            System.out.println("║  3. 食谱管理                              ║");
            System.out.println("║  4. 调班管理                              ║");
            System.out.println("║  5. 考勤管理                              ║");
            System.out.println("║  6. 数据统计                              ║");
            System.out.println("║  7. 修改密码                              ║");
            System.out.println("║  0. 退出登录                              ║");
            System.out.println("╚══════════════════════════════════════════╝");

            int choice = InputUtil.readInt("请选择功能编号：", 0, 7);
            switch (choice) {
                case 1: childView.show(); break;
                case 2: courseView.show(); break;
                case 3: menuView.show(); break;
                case 4: transferView.show(user); break;
                case 5: attendanceView.show(); break;
                case 6: statisticsView.show(); break;
                case 7: loginView.showChangePassword(user); break;
                case 0: return;
            }
        }
    }
}
