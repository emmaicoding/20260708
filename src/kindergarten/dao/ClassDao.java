package kindergarten.dao;

import kindergarten.entity.ClassInfo;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_class_info表的数据库查询操作
 */
public class ClassDao {

    /**
     * 查询所有班级（含当前人数）
     *
     * @return 班级列表
     */
    public List<ClassInfo> selectAll() {
        String sql = "SELECT c.*, " +
                     "(SELECT COUNT(*) FROM t_child ch WHERE ch.class_id = c.id AND ch.status = 1) AS current_count " +
                     "FROM t_class_info c ORDER BY c.id";
        List<ClassInfo> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询班级列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 根据ID查询班级
     *
     * @param id 班级ID
     * @return 班级对象，不存在返回null
     */
    public ClassInfo selectById(int id) {
        String sql = "SELECT c.*, " +
                     "(SELECT COUNT(*) FROM t_child ch WHERE ch.class_id = c.id AND ch.status = 1) AS current_count " +
                     "FROM t_class_info c WHERE c.id = ?";
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
            throw new DataAccessException("查询班级失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 根据年级查询班级列表
     *
     * @param grade 年级名称（大班/中班/小班）
     * @return 班级列表
     */
    public List<ClassInfo> selectByGrade(String grade) {
        String sql = "SELECT c.*, " +
                     "(SELECT COUNT(*) FROM t_child ch WHERE ch.class_id = c.id AND ch.status = 1) AS current_count " +
                     "FROM t_class_info c WHERE c.grade = ? ORDER BY c.id";
        List<ClassInfo> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, grade);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询年级班级失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 将ResultSet映射为ClassInfo对象 */
    private ClassInfo mapRow(ResultSet rs) throws SQLException {
        ClassInfo c = new ClassInfo();
        c.setId(rs.getInt("id"));
        c.setClassName(rs.getString("class_name"));
        c.setGrade(rs.getString("grade"));
        c.setMaxCount(rs.getInt("max_count"));
        c.setCurrentCount(rs.getInt("current_count"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) c.setCreateTime(ct.toLocalDateTime());
        return c;
    }
}
