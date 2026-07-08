package kindergarten.view;

import kindergarten.entity.User;
import kindergarten.util.InitDatabase;
import kindergarten.util.InputUtil;
import kindergarten.util.DBUtil;

/**
 * 主界面/程序入口视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 程序主循环：数据库初始化 → 登录 → 路由到对应角色菜单
 */
public class MainView {
    private final LoginView loginView = new LoginView();
    private final AdminView adminView = new AdminView();
    private final TeacherView teacherView = new TeacherView();

    /**
     * 启动系统主流程
     */
    public void start() {
        // 1. 测试数据库连接
        System.out.println("正在连接数据库...");
        if (!DBUtil.testConnection()) {
            System.out.println("✗ 数据库连接失败！请检查：");
            System.out.println("  1. MySQL服务是否已启动");
            System.out.println("  2. DBUtil中的用户名密码是否正确");
            System.out.println("  3. MySQL Connector/J驱动是否在classpath中");
            return;
        }
        System.out.println("✓ 数据库连接成功！");

        // 2. 初始化数据库（建库、建表、预置数据）
        new InitDatabase().init();

        // 3. 主循环：登录 → 操作 → 退出
        while (true) {
            User user = loginView.show();
            if (user == null) break; // 登录失败，退出程序

            // 根据角色路由到对应菜单
            if (user.getRole() == 1) {
                adminView.show(user);
            } else {
                teacherView.show(user);
            }
            System.out.println("\n已退出登录，返回登录界面...\n");
        }

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    感谢使用幼儿园管理系统，再见！         ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
