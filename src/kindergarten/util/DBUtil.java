package kindergarten.util;

import java.sql.*;

/**
 * 数据库连接工具类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 提供MySQL数据库连接的获取和资源关闭功能，
 *              所有DAO层通过此类获取数据库连接。
 */
public class DBUtil {

    /** MySQL连接地址（不指定数据库，初始化时需要创建数据库） */
    private static final String BASE_URL = "jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true";

    /** 指定kindergarten数据库的连接地址 */
    private static final String URL = "jdbc:mysql://localhost:3306/kindergarten?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true";

    /** 数据库用户名 */
    private static final String USERNAME = "root";

    /** 数据库密码（部署时需根据实际环境修改） */
    private static final String PASSWORD = "lirui520";

    /**
     * 获取基础连接（不指定数据库，用于创建数据库）
     *
     * @return 数据库连接对象
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getBaseConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL, USERNAME, PASSWORD);
    }

    /**
     * 获取kindergarten数据库连接
     *
     * @return 数据库连接对象
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭数据库资源（Connection, Statement, ResultSet）
     *
     * @param conn 数据库连接
     * @param stmt 语句对象
     * @param rs   结果集
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { /* 资源关闭失败不影响主流程 */ }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { /* 资源关闭失败不影响主流程 */ }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { /* 资源关闭失败不影响主流程 */ }
        }
    }

    /**
     * 关闭数据库资源（Connection, Statement）
     *
     * @param conn 数据库连接
     * @param stmt 语句对象
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /**
     * 测试数据库连接是否成功
     *
     * @return 连接成功返回true，否则返回false
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
