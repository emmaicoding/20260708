package kindergarten.dao;

import kindergarten.entity.Attendance;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 考勤数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_attendance表的数据库CRUD操作
 */
public class AttendanceDao {

    /**
     * 插入或更新考勤记录（同一幼儿同一天只有一条记录）
     *
     * @param att 考勤实体
     * @return 影响行数
     */
    public int insertOrUpdate(Attendance att) {
        String sql = "INSERT INTO t_attendance(child_id, attend_date, status, remark) VALUES(?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE status=?, remark=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, att.getChildId());
            ps.setDate(2, Date.valueOf(att.getAttendDate()));
            ps.setInt(3, att.getStatus());
            ps.setString(4, att.getRemark());
            ps.setInt(5, att.getStatus());
            ps.setString(6, att.getRemark());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("记录考勤失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 查询某班某日的考勤记录（含幼儿姓名）
     *
     * @param classId 班级ID
     * @param date    考勤日期
     * @return 考勤列表
     */
    public List<Attendance> selectByClassAndDate(int classId, LocalDate date) {
        String sql = "SELECT a.*, ch.name AS child_name FROM t_attendance a " +
                     "JOIN t_child ch ON a.child_id = ch.id " +
                     "WHERE ch.class_id = ? AND a.attend_date = ? AND ch.status = 1 " +
                     "ORDER BY ch.id";
        List<Attendance> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, classId);
            ps.setDate(2, Date.valueOf(date));
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询考勤失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询某个幼儿在指定日期范围内的考勤记录
     *
     * @param childId 幼儿ID
     * @param start   开始日期
     * @param end     结束日期
     * @return 考勤列表
     */
    public List<Attendance> selectByChildAndDateRange(int childId, LocalDate start, LocalDate end) {
        String sql = "SELECT a.*, ch.name AS child_name FROM t_attendance a " +
                     "JOIN t_child ch ON a.child_id = ch.id " +
                     "WHERE a.child_id = ? AND a.attend_date BETWEEN ? AND ? " +
                     "ORDER BY a.attend_date";
        List<Attendance> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询考勤记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 统计某班在指定日期范围内的出勤情况
     * 返回数组：[总记录数, 出勤数, 缺勤数, 请假数, 迟到数]
     *
     * @param classId 班级ID
     * @param start   开始日期
     * @param end     结束日期
     * @return 统计数组
     */
    public int[] countByClassAndDateRange(int classId, LocalDate start, LocalDate end) {
        String sql = "SELECT a.status, COUNT(*) FROM t_attendance a " +
                     "JOIN t_child ch ON a.child_id = ch.id " +
                     "WHERE ch.class_id = ? AND a.attend_date BETWEEN ? AND ? " +
                     "GROUP BY a.status";
        int[] result = new int[5]; // [total, 出勤, 缺勤, 请假, 迟到]
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, classId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            rs = ps.executeQuery();
            while (rs.next()) {
                int status = rs.getInt("status");
                int count = rs.getInt(2);
                result[status] = count;
                result[0] += count;
            }
        } catch (SQLException e) {
            throw new DataAccessException("统计考勤失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return result;
    }

    /**
     * 删除幼儿的所有考勤记录（删除幼儿时级联清理）
     *
     * @param childId 幼儿ID
     * @return 影响行数
     */
    public int deleteByChildId(int childId) {
        String sql = "DELETE FROM t_attendance WHERE child_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, childId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("清除考勤记录失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 将ResultSet映射为Attendance对象 */
    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setChildId(rs.getInt("child_id"));
        a.setChildName(rs.getString("child_name"));
        Date ad = rs.getDate("attend_date");
        if (ad != null) a.setAttendDate(ad.toLocalDate());
        a.setStatus(rs.getInt("status"));
        a.setRemark(rs.getString("remark"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) a.setCreateTime(ct.toLocalDateTime());
        return a;
    }
}
