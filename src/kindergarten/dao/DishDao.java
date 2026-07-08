package kindergarten.dao;

import kindergarten.entity.Dish;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_dish表的数据库CRUD操作
 */
public class DishDao {

    /**
     * 添加菜品
     *
     * @param dish 菜品实体
     * @return 新增记录ID
     */
    public int insert(Dish dish) {
        String sql = "INSERT INTO t_dish(dish_name, dish_type) VALUES(?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dish.getDishName());
            ps.setInt(2, dish.getDishType());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("添加菜品失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return -1;
    }

    /**
     * 根据ID查询菜品
     *
     * @param id 菜品ID
     * @return 菜品对象
     */
    public Dish selectById(int id) {
        String sql = "SELECT * FROM t_dish WHERE id = ?";
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
            throw new DataAccessException("查询菜品失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * 查询所有菜品
     *
     * @return 菜品列表
     */
    public List<Dish> selectAll() {
        String sql = "SELECT * FROM t_dish ORDER BY dish_type, id";
        List<Dish> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询菜品列表失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 按类型查询菜品
     *
     * @param dishType 菜品类型（1~5）
     * @return 菜品列表
     */
    public List<Dish> selectByType(int dishType) {
        String sql = "SELECT * FROM t_dish WHERE dish_type = ? ORDER BY id";
        List<Dish> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dishType);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询菜品失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 修改菜品
     *
     * @param dish 菜品实体
     * @return 影响行数
     */
    public int update(Dish dish) {
        String sql = "UPDATE t_dish SET dish_name=?, dish_type=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, dish.getDishName());
            ps.setInt(2, dish.getDishType());
            ps.setInt(3, dish.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("修改菜品失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 删除菜品
     *
     * @param id 菜品ID
     * @return 影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM t_dish WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除菜品失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 将ResultSet映射为Dish对象 */
    private Dish mapRow(ResultSet rs) throws SQLException {
        Dish d = new Dish();
        d.setId(rs.getInt("id"));
        d.setDishName(rs.getString("dish_name"));
        d.setDishType(rs.getInt("dish_type"));
        Timestamp ct = rs.getTimestamp("create_time");
        if (ct != null) d.setCreateTime(ct.toLocalDateTime());
        return d;
    }
}
