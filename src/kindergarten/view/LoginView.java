package kindergarten.view;

import kindergarten.entity.User;
import kindergarten.service.UserService;
import kindergarten.util.InputUtil;

/**
 * 登录界面
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理用户登录和修改密码的界面交互
 */
public class LoginView {
    private final UserService userService = new UserService();

    /**
     * 显示登录界面，返回登录成功的用户
     *
     * @return 登录成功返回User对象，选择退出返回null
     */
    public User show() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║        幼儿园管理系统 v1.0                ║");
        System.out.println("╚══════════════════════════════════════════╝");

        int attempts = 0;
        while (attempts < 3) {
            String username = InputUtil.readNonEmpty("  请输入用户名：");
            System.out.print("  请输入密码：");
            String password = InputUtil.getScanner().nextLine();

            User user = userService.login(username, password);
            if (user != null) {
                System.out.println("\n  ✓ 登录成功！欢迎，" + user.getRoleName() + " " + user.getRealName());
                return user;
            }
            attempts++;
            System.out.println("  ✗ 用户名或密码错误（剩余尝试：" + (3 - attempts) + "次）");
        }
        System.out.println("\n  ✗ 登录失败次数过多，程序退出");
        return null;
    }

    /**
     * 显示修改密码界面
     *
     * @param currentUser 当前登录用户
     */
    public void showChangePassword(User currentUser) {
        System.out.println("\n══════ 修改密码 ══════");
        String oldPwd = InputUtil.readNonEmpty("  请输入旧密码：");
        String newPwd = InputUtil.readNonEmpty("  请输入新密码（至少4位）：");
        String confirmPwd = InputUtil.readNonEmpty("  请确认新密码：");

        if (!newPwd.equals(confirmPwd)) {
            System.out.println("  ✗ 两次输入的新密码不一致");
            return;
        }
        String result = userService.changePassword(currentUser.getId(), oldPwd, newPwd);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }
}
