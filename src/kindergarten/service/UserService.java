package kindergarten.service;

import kindergarten.dao.UserDao;
import kindergarten.entity.User;
import kindergarten.exception.DataAccessException;
import kindergarten.util.PasswordUtil;

/**
 * 用户业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理用户登录认证和密码修改业务
 */
public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回User对象，失败返回null
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.isEmpty()) {
            return null;
        }
        try {
            return userDao.login(username.trim(), password);
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 结果描述字符串
     */
    public String changePassword(int userId, String oldPassword, String newPassword) {
        try {
            // 校验旧密码
            User user = userDao.selectById(userId);
            if (user == null) {
                return "用户不存在";
            }
            if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                return "旧密码错误";
            }
            if (newPassword == null || newPassword.length() < 4) {
                return "新密码长度不能少于4位";
            }
            if (newPassword.equals(oldPassword)) {
                return "新密码不能与旧密码相同";
            }
            int rows = userDao.updatePassword(userId, newPassword);
            return rows > 0 ? "密码修改成功" : "密码修改失败";
        } catch (DataAccessException e) {
            return "密码修改失败：" + e.getMessage();
        }
    }
}
