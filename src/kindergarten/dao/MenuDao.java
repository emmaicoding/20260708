package kindergarten.dao;

import kindergarten.entity.WeeklyMenu;
import kindergarten.exception.DataAccessException;
import kindergarten.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 食谱数据访问对象
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装t_weekly_menu表的数据库操作
 */
public class MenuDao {

    /**
     * 添加食谱记录
     *
     * @param menu 食谱实体
     * @return 影响行数
     */
    public int insert(WeeklyMenu menu) {
        String sql = "INSERT INTO t_weekly_menu(week_start, day_of_week, meal_type, dish_id) VALUES(?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(menu.getWeekStart()));
            ps.setInt(2, menu.getDayOfWeek());
            ps.setInt(3, menu.getMealType());
            ps.setInt(4, menu.getDishId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("添加食谱失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 查询指定周的食谱（含菜品名称）
     *
     * @param weekStart 周起始日期（周一）
     * @return 食谱列表
     */
    public List<WeeklyMenu> selectByWeek(LocalDate weekStart) {
        String sql = "SELECT wm.*, d.dish_name, d.dish_type FROM t_weekly_menu wm " +
                     "JOIN t_dish d ON wm.dish_id = d.id " +
                     "WHERE wm.week_start = ? ORDER BY wm.day_of_week, wm.meal_type";
        List<WeeklyMenu> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(weekStart));
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DataAccessException("查询食谱失败", e);
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 查询本周食谱（自动计算本周一日期）
     *
     * @return 食谱列表
     */
    public List<WeeklyMenu> selectCurrentWeek() {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return selectByWeek(monday);
    }

    /**
     * 删除指定周的食谱（用于重新排餐）
     *
     * @param weekStart 周起始日期
     * @return 影响行数
     */
    public int deleteByWeek(LocalDate weekStart) {
        String sql = "DELETE FROM t_weekly_menu WHERE week_start = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(weekStart));
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("删除食谱失败", e);
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /**
     * 批量插入一周食谱
     *
     * @param menus 食谱列表
     * @return 成功插入条数
     */
    public int insertBatch(List<WeeklyMenu> menus) {
        int count = 0;
        for (WeeklyMenu menu : menus) {
            if (insert(menu) > 0) count++;
        }
        return count;
    }

    /** 将ResultSet映射为WeeklyMenu对象 */
    private WeeklyMenu mapRow(ResultSet rs) throws SQLException {
        WeeklyMenu m = new WeeklyMenu();
        m.setId(rs.getInt("id"));
        Date ws = rs.getDate("week_start");
        if (ws != null) m.setWeekStart(ws.toLocalDate());
        m.setDayOfWeek(rs.getInt("day_of_week"));
        m.setMealType(rs.getInt("meal_type"));
        m.setDishId(rs.getInt("dish_id"));
        m.setDishName(rs.getString("dish_name"));
        m.setDishType(rs.getInt("dish_type"));
        return m;
    }
}
