package kindergarten.entity;

import java.time.LocalDate;

/**
 * 每周食谱实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_weekly_menu表，记录每周每天的菜品安排
 */
public class WeeklyMenu {
    private Integer id;          // 记录ID
    private LocalDate weekStart; // 周起始日期（周一）
    private Integer dayOfWeek;   // 星期几：1=周一 ... 5=周五
    private Integer mealType;    // 餐次：1=早餐 2=午餐 3=晚餐
    private Integer dishId;      // 菜品ID
    private String dishName;     // 菜品名称（关联查询用）
    private Integer dishType;    // 菜品类型（关联查询用）

    public WeeklyMenu() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Integer getMealType() { return mealType; }
    public void setMealType(Integer mealType) { this.mealType = mealType; }

    public Integer getDishId() { return dishId; }
    public void setDishId(Integer dishId) { this.dishId = dishId; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public Integer getDishType() { return dishType; }
    public void setDishType(Integer dishType) { this.dishType = dishType; }

    /** 获取星期中文名 */
    public String getDayName() {
        String[] days = {"", "周一", "周二", "周三", "周四", "周五"};
        return (dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 5) ? days[dayOfWeek] : "未知";
    }

    /** 获取餐次中文名 */
    public String getMealName() {
        switch (mealType) {
            case 1: return "早餐";
            case 2: return "午餐";
            case 3: return "晚餐";
            default: return "未知";
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s：%s", getDayName(), getMealName(), dishName);
    }
}
