package kindergarten.dao;

import kindergarten.entity.TransferLog;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 调班记录数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_transfer_log表的数据库操作
 */
public class TransferLogDao {

    /**
     * 添加调班记录（使用独立连接）
     *
     * @param log 调班记录实体
     * @return 新增记录ID
     */
    public int insert(TransferLog log) {
        String sql = "INSERT INTO t_transfer_log(child_id, old_class_id, new_class_id, operator_id, remark) " +
                     "VALUES(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, log.getChildId());
            ps.setInt(2, log.getOldClassId());
            ps.setInt(3, log.getNewClassId());
            ps.setInt(4, log.getOperatorId());
            ps.setString(5, log.getRemark());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("记录调班失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return -1;
    }

    /**
     * 添加调班记录（支持外部传入连接以实现事务）
     *
     * @param conn 数据库连接（由调用方管理事务和关闭）
     * @param log  调班记录实体
     * @return 新增记录ID
     * @throws SQLException SQL执行失败时抛出
     */
    public int insert(Connection conn, TransferLog log) throws SQLException {
        String sql = "INSERT INTO t_transfer_log(child_id, old_class_id, new_class_id, operator_id, remark) " +
                     "VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getChildId());
            ps.setInt(2, log.getOldClassId());
            ps.setInt(3, log.getNewClassId());
            ps.setInt(4, log.getOperatorId());
            ps.setString(5, log.getRemark());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /**
     * 查询所有调班记录（含幼儿姓名、班级名称、操作人姓名）
     *
     * @return 调班记录列表
     */
    public List<TransferLog> selectAll() {
        String sql = "SELECT tl.*, ch.name AS child_name, " +
                     "oc.class_name AS old_class_name, nc.class_name AS new_class_name, " +
                     "u.real_name AS operator_name " +
                     "FROM t_transfer_log tl " +
                     "JOIN t_child ch ON tl.child_id = ch.id " +
                     "JOIN t_class_info oc ON tl.old_class_id = oc.id " +
                     "JOIN t_class_info nc ON tl.new_class_id = nc.id " +
                     "JOIN t_user u ON tl.operator_id = u.id " +
                     "ORDER BY tl.transfer_date DESC";
        List<TransferLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询调班记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 根据幼儿ID查询调班记录
     *
     * @param childId 幼儿ID
     * @return 调班记录列表
     */
    public List<TransferLog> selectByChildId(int childId) {
        String sql = "SELECT tl.*, ch.name AS child_name, " +
                     "oc.class_name AS old_class_name, nc.class_name AS new_class_name, " +
                     "u.real_name AS operator_name " +
                     "FROM t_transfer_log tl " +
                     "JOIN t_child ch ON tl.child_id = ch.id " +
                     "JOIN t_class_info oc ON tl.old_class_id = oc.id " +
                     "JOIN t_class_info nc ON tl.new_class_id = nc.id " +
                     "JOIN t_user u ON tl.operator_id = u.id " +
                     "WHERE tl.child_id = ? ORDER BY tl.transfer_date DESC";
        List<TransferLog> list = new ArrayList<>();
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
            throw new DataAccessException("查询幼儿调班记录失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /** 将ResultSet映射为TransferLog对象 */
    private TransferLog mapRow(ResultSet rs) throws SQLException {
        TransferLog tl = new TransferLog();
        tl.setId(rs.getInt("id"));
        tl.setChildId(rs.getInt("child_id"));
        tl.setChildName(rs.getString("child_name"));
        tl.setOldClassId(rs.getInt("old_class_id"));
        tl.setOldClassName(rs.getString("old_class_name"));
        tl.setNewClassId(rs.getInt("new_class_id"));
        tl.setNewClassName(rs.getString("new_class_name"));
        tl.setOperatorId(rs.getInt("operator_id"));
        tl.setOperatorName(rs.getString("operator_name"));
        Timestamp td = rs.getTimestamp("transfer_date");
        if (td != null) tl.setTransferDate(td.toLocalDateTime());
        tl.setRemark(rs.getString("remark"));
        return tl;
    }
}
