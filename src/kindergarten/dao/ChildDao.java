package kindergarten.dao;

import kindergarten.entity.Child;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 幼儿数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_child表的数据库CRUD操作
 */
public class ChildDao {

    /**
     * 添加幼儿
     *
     * @param child 幼儿实体对象
     * @return 新增记录的ID，失败返回-1
     */
    public int insert(Child child) {
        String sql = "INSERT INTO t_child(name,gender,birth_date,parent_name,parent_phone,class_id,enrollment_date,status) " +
                     "VALUES(?,?,?,?,?,?,?,1)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, child.getName());
            ps.setString(2, child.getGender());
            ps.setDate(3, Date.valueOf(child.getBirthDate()));
            ps.setString(4, child.getParentName());
            ps.setString(5, child.getParentPhone());
            ps.setInt(6, child.getClassId());
            ps.setDate(7, Date.valueOf(child.getEnrollmentDate()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                child.setId(generatedId);
                return generatedId;
            }
        } catch (SQLException e) {
            throw new DataAccessException("添加幼儿失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return -1;
    }

    /**
     * 根据ID查询幼儿（含班级名称）
     *
     * @param id 幼儿ID
     * @return 幼儿对象，不存在返回null
     */
    public Child selectById(int id) {
        String sql = "SELECT ch.*, cl.class_name FROM t_child ch " +
                     "JOIN t_class_info cl ON ch.class_id = cl.id WHERE ch.id = ?";
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
            throw new DataAccessException("查询幼儿失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 根据姓名模糊查询幼儿
     *
     * @param name 姓名关键字
     * @return 幼儿列表
     */
    public List<Child> selectByName(String name) {
        String sql = "SELECT ch.*, cl.class_name FROM t_child ch " +
                     "JOIN t_class_info cl ON ch.class_id = cl.id " +
                     "WHERE ch.name LIKE ? AND ch.status = 1 ORDER BY ch.id";
        List<Child> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("搜索幼儿失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 根据班级ID查询所有在园幼儿
     *
     * @param classId 班级ID
     * @return 幼儿列表
     */
    public List<Child> selectByClassId(int classId) {
        String sql = "SELECT ch.*, cl.class_name FROM t_child ch " +
                     "JOIN t_class_info cl ON ch.class_id = cl.id " +
                     "WHERE ch.class_id = ? AND ch.status = 1 ORDER BY ch.id";
        List<Child> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, classId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询班级幼儿失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询所有在园幼儿
     *
     * @return 幼儿列表
     */
    public List<Child> selectAll() {
        String sql = "SELECT ch.*, cl.class_name FROM t_child ch " +
                     "JOIN t_class_info cl ON ch.class_id = cl.id " +
                     "WHERE ch.status = 1 ORDER BY ch.class_id, ch.id";
        List<Child> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询幼儿列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 修改幼儿基本信息（不含班级，班级通过调班接口修改）
     *
     * @param child 幼儿实体
     * @return 影响行数
     */
    public int update(Child child) {
        String sql = "UPDATE t_child SET name=?, gender=?, birth_date=?, parent_name=?, parent_phone=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, child.getName());
            ps.setString(2, child.getGender());
            ps.setDate(3, Date.valueOf(child.getBirthDate()));
            ps.setString(4, child.getParentName());
            ps.setString(5, child.getParentPhone());
            ps.setInt(6, child.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改幼儿信息失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 更新幼儿班级（调班用，使用独立连接）
     *
     * @param childId  幼儿ID
     * @param newClassId 新班级ID
     * @return 影响行数
     */
    public int updateClass(int childId, int newClassId) {
        String sql = "UPDATE t_child SET class_id = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, newClassId);
            ps.setInt(2, childId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("调班失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 更新幼儿班级（调班用，支持外部传入连接以实现事务）
     *
     * @param conn      数据库连接（由调用方管理事务和关闭）
     * @param childId   幼儿ID
     * @param newClassId 新班级ID
     * @return 影响行数
     * @throws SQLException SQL执行失败时抛出
     */
    public int updateClass(Connection conn, int childId, int newClassId) throws SQLException {
        String sql = "UPDATE t_child SET class_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newClassId);
            ps.setInt(2, childId);
            return ps.executeUpdate();
        }
    }

    /**
     * 软删除幼儿（设置status=0）
     *
     * @param id 幼儿ID
     * @return 影响行数
     */
    public int delete(int id) {
        String sql = "UPDATE t_child SET status = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除幼儿失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 统计班级当前在园人数
     *
     * @param classId 班级ID
     * @return 在园人数
     */
    public int countByClassId(int classId) {
        String sql = "SELECT COUNT(*) FROM t_child WHERE class_id = ? AND status = 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, classId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("统计人数失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return 0;
    }

    /** 将ResultSet映射为Child对象 */
    private Child mapRow(ResultSet rs) throws SQLException {
        Child c = new Child();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setGender(rs.getString("gender"));
        Date bd = rs.getDate("birth_date");
        if (bd != null) c.setBirthDate(bd.toLocalDate());
        c.setParentName(rs.getString("parent_name"));
        c.setParentPhone(rs.getString("parent_phone"));
        c.setClassId(rs.getInt("class_id"));
        c.setClassName(rs.getString("class_name"));
        Date ed = rs.getDate("enrollment_date");
        if (ed != null) c.setEnrollmentDate(ed.toLocalDate());
        c.setStatus(rs.getInt("status"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) c.setCreateTime(ct.toLocalDateTime());
        return c;
    }
}
