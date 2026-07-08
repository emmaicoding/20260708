package kindergarten;

import kindergarten.dao.UserDao;
import kindergarten.entity.User;
import kindergarten.exception.DataAccessException;
import kindergarten.util.InitDatabase;

import java.util.List;

/**
 * UserDao测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试用户登录、查询、密码修改等DAO操作
 */
public class UserDaoTest {

    public static void main(String[] args) {
        System.out.println("══════ UserDao测试 ══════\n");
        new InitDatabase().init();

        UserDao dao = new UserDao();
        int passed = 0, failed = 0;

        // 测试1：管理员登录
        System.out.println("[测试1] 管理员登录");
        User admin = dao.login("admin", "admin123");
        if (admin != null && admin.getRole() == 1) {
            System.out.printf("  ✓ PASS：登录成功 - %s（%s）\n", admin.getRealName(), admin.getRoleName());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：管理员登录失败");
            failed++;
        }

        // 测试2：教师登录
        System.out.println("[测试2] 教师登录");
        User teacher = dao.login("teacher01", "123456");
        if (teacher != null && teacher.getRole() == 2 && teacher.getClassId() != null) {
            System.out.printf("  ✓ PASS：登录成功 - %s（%s，%s）\n",
                teacher.getRealName(), teacher.getRoleName(), teacher.getClassName());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：教师登录失败");
            failed++;
        }

        // 测试3：错误密码登录
        System.out.println("[测试3] 错误密码登录");
        User wrong = dao.login("admin", "wrongpassword");
        if (wrong == null) {
            System.out.println("  ✓ PASS：错误密码返回null");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：错误密码应返回null");
            failed++;
        }

        // 测试4：根据ID查询
        System.out.println("[测试4] 根据ID查询");
        User user = dao.selectById(1);
        if (user != null && "admin".equals(user.getUsername())) {
            System.out.printf("  ✓ PASS：查询到用户 %s\n", user.getRealName());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：根据ID查询失败");
            failed++;
        }

        // 测试5：查询所有用户
        System.out.println("[测试5] 查询所有用户");
        List<User> users = dao.selectAll();
        if (users.size() == 10) {
            System.out.printf("  ✓ PASS：共%d个用户\n", users.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望10个用户，实际%d个\n", users.size());
            failed++;
        }

        // 测试6：修改密码
        System.out.println("[测试6] 修改密码");
        int rows = dao.updatePassword(1, "newpass123");
        // 还原密码
        dao.updatePassword(1, "admin123");
        if (rows > 0) {
            System.out.println("  ✓ PASS：密码修改成功");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：密码修改失败");
            failed++;
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
