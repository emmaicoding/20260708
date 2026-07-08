package kindergarten.service;

import kindergarten.dao.DishDao;
import kindergarten.dao.MenuDao;
import kindergarten.entity.Dish;
import kindergarten.entity.WeeklyMenu;
import kindergarten.exception.DataAccessException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 食谱业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理菜品库管理和每周排餐业务
 */
public class MenuService {
    private final DishDao dishDao = new DishDao();
    private final MenuDao menuDao = new MenuDao();

    // ==================== 菜品库管理 ====================

    /**
     * 添加菜品
     *
     * @param dish 菜品实体
     * @return 结果描述
     */
    public String addDish(Dish dish) {
        if (dish.getDishName() == null || dish.getDishName().trim().isEmpty()) {
            return "菜品名称不能为空";
        }
        if (dish.getDishType() == null || dish.getDishType() < 1 || dish.getDishType() > 5) {
            return "请选择正确的菜品类型（1主食/2荤菜/3素菜/4汤/5水果）";
        }
        try {
            int id = dishDao.insert(dish);
            return id > 0 ? "菜品添加成功（ID：" + id + "）" : "添加失败";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }

    /**
     * 修改菜品
     *
     * @param dish 菜品实体
     * @return 结果描述
     */
    public String updateDish(Dish dish) {
        if (dish.getId() == null) return "菜品ID无效";
        try {
            Dish existing = dishDao.selectById(dish.getId());
            if (existing == null) return "菜品不存在";
            int rows = dishDao.update(dish);
            return rows > 0 ? "修改成功" : "修改失败";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }

    /**
     * 删除菜品
     *
     * @param dishId 菜品ID
     * @return 结果描述
     */
    public String deleteDish(int dishId) {
        try {
            Dish existing = dishDao.selectById(dishId);
            if (existing == null) return "菜品不存在";
            int rows = dishDao.delete(dishId);
            return rows > 0 ? "菜品已删除" : "删除失败（可能正在被食谱引用）";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }

    /**
     * 查询所有菜品
     *
     * @return 菜品列表
     */
    public List<Dish> getAllDishes() {
        try {
            return dishDao.selectAll();
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 按类型查询菜品
     *
     * @param dishType 菜品类型
     * @return 菜品列表
     */
    public List<Dish> getDishesByType(int dishType) {
        try {
            return dishDao.selectByType(dishType);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    // ==================== 食谱管理 ====================

    /**
     * 排餐：为指定周某天某餐添加菜品
     *
     * @param weekStart 周一日期
     * @param dayOfWeek 星期几（1~5）
     * @param mealType  餐次（1早/2中/3晚）
     * @param dishId    菜品ID
     * @return 结果描述
     */
    public String arrangeMenu(LocalDate weekStart, int dayOfWeek, int mealType, int dishId) {
        try {
            Dish dish = dishDao.selectById(dishId);
            if (dish == null) return "菜品不存在";
            WeeklyMenu menu = new WeeklyMenu();
            menu.setWeekStart(weekStart);
            menu.setDayOfWeek(dayOfWeek);
            menu.setMealType(mealType);
            menu.setDishId(dishId);
            int rows = menuDao.insert(menu);
            return rows > 0 ? "排餐成功" : "排餐失败";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }

    /**
     * 批量排餐
     *
     * @param menus 食谱列表
     * @return 成功条数
     */
    public int batchArrangeMenu(List<WeeklyMenu> menus) {
        try {
            return menuDao.insertBatch(menus);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 清空某周食谱并重新排餐
     *
     * @param weekStart 周一日期
     * @param menus     新食谱列表
     * @return 成功条数
     */
    public int replaceWeekMenu(LocalDate weekStart, List<WeeklyMenu> menus) {
        try {
            menuDao.deleteByWeek(weekStart);
            return menuDao.insertBatch(menus);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 查询本周食谱
     *
     * @return 食谱列表
     */
    public List<WeeklyMenu> getCurrentWeekMenu() {
        try {
            return menuDao.selectCurrentWeek();
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 查询指定周食谱
     *
     * @param weekStart 周一日期
     * @return 食谱列表
     */
    public List<WeeklyMenu> getWeekMenu(LocalDate weekStart) {
        try {
            return menuDao.selectByWeek(weekStart);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 复制上周食谱到本周
     *
     * @return 结果描述
     */
    public String copyLastWeekMenu() {
        try {
            LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
            LocalDate lastMonday = thisMonday.minusWeeks(1);
            List<WeeklyMenu> lastWeek = menuDao.selectByWeek(lastMonday);
            if (lastWeek.isEmpty()) {
                return "上周没有食谱可复制";
            }
            // 生成本周食谱
            List<WeeklyMenu> thisWeek = new ArrayList<>();
            for (WeeklyMenu m : lastWeek) {
                WeeklyMenu newMenu = new WeeklyMenu();
                newMenu.setWeekStart(thisMonday);
                newMenu.setDayOfWeek(m.getDayOfWeek());
                newMenu.setMealType(m.getMealType());
                newMenu.setDishId(m.getDishId());
                thisWeek.add(newMenu);
            }
            menuDao.deleteByWeek(thisMonday);
            int count = menuDao.insertBatch(thisWeek);
            return "已复制上周食谱到本周（" + count + "条）";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }
}
