package kindergarten.dao;

import kindergarten.entity.Course;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_course表的数据库CRUD操作
 */
public class CourseDao {

    /**
     * 添加课程
     *
     * @param course 课程实体
     * @return 新增记录ID，失败返回-1
     */
    public int insert(Course course) {
        String sql = "INSERT INTO t_course(course_name, max_count, description) VALUES(?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getCourseName());
            ps.setInt(2, course.getMaxCount());
            ps.setString(3, course.getDescription());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("添加课程失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return -1;
    }

    /**
     * 根据ID查询课程（含当前选课人数）
     *
     * @param id 课程ID
     * @return 课程对象
     */
    public Course selectById(int id) {
        String sql = "SELECT c.*, " +
                     "(SELECT COUNT(*) FROM t_child_course cc WHERE cc.course_id = c.id) AS current_count " +
                     "FROM t_course c WHERE c.id = ?";
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
            throw new DataAccessException("查询课程失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 查询所有课程（含当前选课人数）
     *
     * @return 课程列表
     */
    public List<Course> selectAll() {
        String sql = "SELECT c.*, " +
                     "(SELECT COUNT(*) FROM t_child_course cc WHERE cc.course_id = c.id) AS current_count " +
                     "FROM t_course c ORDER BY c.id";
        List<Course> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询课程列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 修改课程信息
     *
     * @param course 课程实体
     * @return 影响行数
     */
    public int update(Course course) {
        String sql = "UPDATE t_course SET course_name=?, max_count=?, description=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, course.getCourseName());
            ps.setInt(2, course.getMaxCount());
            ps.setString(3, course.getDescription());
            ps.setInt(4, course.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改课程失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM t_course WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除课程失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 将ResultSet映射为Course对象 */
    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCourseName(rs.getString("course_name"));
        c.setMaxCount(rs.getInt("max_count"));
        c.setDescription(rs.getString("description"));
        c.setCurrentCount(rs.getInt("current_count"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) c.setCreateTime(ct.toLocalDateTime());
        return c;
    }
}
