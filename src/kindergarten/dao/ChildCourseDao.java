package kindergarten.dao;

import kindergarten.entity.ChildCourse;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 选课关系数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_child_course表的数据库操作
 */
public class ChildCourseDao {

    /**
     * 添加选课记录
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 影响行数
     */
    public int insert(int childId, int courseId) {
        String sql = "INSERT INTO t_child_course(child_id, course_id) VALUES(?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            ps.setInt(2, courseId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("选课失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除选课记录
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 影响行数
     */
    public int delete(int childId, int courseId) {
        String sql = "DELETE FROM t_child_course WHERE child_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            ps.setInt(2, courseId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("退课失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 根据幼儿ID查询已选课程（含课程名称）
     *
     * @param childId 幼儿ID
     * @return 选课列表
     */
    public List<ChildCourse> selectByChildId(int childId) {
        String sql = "SELECT cc.*, ch.name AS child_name, co.course_name " +
                     "FROM t_child_course cc " +
                     "JOIN t_child ch ON cc.child_id = ch.id " +
                     "JOIN t_course co ON cc.course_id = co.id " +
                     "WHERE cc.child_id = ? ORDER BY cc.course_id";
        List<ChildCourse> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询选课失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 根据课程ID查询选课幼儿（含幼儿姓名）
     *
     * @param courseId 课程ID
     * @return 选课列表
     */
    public List<ChildCourse> selectByCourseId(int courseId) {
        String sql = "SELECT cc.*, ch.name AS child_name, co.course_name " +
                     "FROM t_child_course cc " +
                     "JOIN t_child ch ON cc.child_id = ch.id " +
                     "JOIN t_course co ON cc.course_id = co.id " +
                     "WHERE cc.course_id = ? ORDER BY ch.class_id, ch.id";
        List<ChildCourse> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询课程学员失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 统计幼儿已选课程数
     *
     * @param childId 幼儿ID
     * @return 已选课程数
     */
    public int countByChildId(int childId) {
        String sql = "SELECT COUNT(*) FROM t_child_course WHERE child_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("统计选课数失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /**
     * 统计课程当前选课人数
     *
     * @param courseId 课程ID
     * @return 选课人数
     */
    public int countByCourseId(int courseId) {
        String sql = "SELECT COUNT(*) FROM t_child_course WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("统计课程人数失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /**
     * 检查幼儿是否已选某课程
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 已选返回true
     */
    public boolean exists(int childId, int courseId) {
        String sql = "SELECT COUNT(*) FROM t_child_course WHERE child_id = ? AND course_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DataAccessException("检查选课失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return false;
    }

    /**
     * 删除幼儿的所有选课记录（删除幼儿时级联清理）
     *
     * @param childId 幼儿ID
     * @return 影响行数
     */
    public int deleteByChildId(int childId) {
        String sql = "DELETE FROM t_child_course WHERE child_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("清除选课记录失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 将ResultSet映射为ChildCourse对象 */
    private ChildCourse mapRow(ResultSet rs) throws SQLException {
        ChildCourse cc = new ChildCourse();
        cc.setId(rs.getInt("id"));
        cc.setChildId(rs.getInt("child_id"));
        cc.setCourseId(rs.getInt("course_id"));
        cc.setChildName(rs.getString("child_name"));
        cc.setCourseName(rs.getString("course_name"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) cc.setCreateTime(ct.toLocalDateTime());
        return cc;
    }
}
