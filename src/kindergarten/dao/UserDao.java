package kindergarten.dao;

import kindergarten.entity.User;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;
import kindergarten.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_user表的数据库操作
 */
public class UserDao {

    /**
     * 根据用户名查询用户并验证密码（登录验证）
     *
     * @param username 用户名
     * @param password 明文密码（通过PasswordUtil.verify与存储的哈希比对）
     * @return 用户对象，验证失败返回null
     */
    public User login(String username, String password) {
        String sql = "SELECT u.*, c.class_name FROM t_user u " +
                     "LEFT JOIN t_class_info c ON u.class_id = c.id " +
                     "WHERE u.username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                User user = mapRow(rs);
                // 使用SHA-256哈希验证密码
                if (PasswordUtil.verify(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("登录查询失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    public User selectById(int id) {
        String sql = "SELECT u.*, c.class_name FROM t_user u " +
                     "LEFT JOIN t_class_info c ON u.class_id = c.id WHERE u.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new DataAccessException("查询用户失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<User> selectAll() {
        String sql = "SELECT u.*, c.class_name FROM t_user u " +
                     "LEFT JOIN t_class_info c ON u.class_id = c.id ORDER BY u.role, u.id";
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询用户列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 修改用户密码（存储SHA-256哈希值）
     *
     * @param userId      用户ID
     * @param newPassword 新密码（明文，方法内部会进行哈希处理）
     * @return 影响行数
     */
    public int updatePassword(int userId, String newPassword) {
        String sql = "UPDATE t_user SET password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, PasswordUtil.hash(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改密码失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 将ResultSet映射为User对象 */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRealName(rs.getString("real_name"));
        u.setRole(rs.getInt("role"));
        u.setClassId(rs.getObject("class_id") != null ? rs.getInt("class_id") : null);
        u.setClassName(rs.getString("class_name"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) u.setCreateTime(ct.toLocalDateTime());
        return u;
    }
}
