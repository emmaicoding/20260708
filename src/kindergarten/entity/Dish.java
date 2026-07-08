package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 菜品实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_dish表，存储菜品库信息
 */
public class Dish {
    private Integer id;              // 菜品ID
    private String dishName;         // 菜品名称
    private Integer dishType;        // 类型：1主食 2荤菜 3素菜 4汤 5水果
    private LocalDateTime createTime;// 创建时间

    public Dish() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public Integer getDishType() { return dishType; }
    public void setDishType(Integer dishType) { this.dishType = dishType; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /** 获取菜品类型中文名 */
    public String getDishTypeName() {
        switch (dishType) {
            case 1: return "主食";
            case 2: return "荤菜";
            case 3: return "素菜";
            case 4: return "汤";
            case 5: return "水果";
            default: return "未知";
        }
    }

    @Override
    public String toString() {
        return String.format("Dish{id=%d, name='%s', type='%s'}", id, dishName, getDishTypeName());
    }
}
