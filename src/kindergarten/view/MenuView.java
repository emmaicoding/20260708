package kindergarten.view;

import kindergarten.entity.Dish;
import kindergarten.entity.WeeklyMenu;
import kindergarten.service.MenuService;
import kindergarten.util.InputUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 食谱管理视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，支持菜品库管理和每周排餐
 */
public class MenuView {
    private final MenuService menuService = new MenuService();

    /**
     * 显示食谱管理菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n══════ 食谱管理 ══════");
            System.out.println("  1. 查看本周食谱");
            System.out.println("  2. 菜品库管理");
            System.out.println("  3. 排餐（编辑本周食谱）");
            System.out.println("  4. 复制上周食谱");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 4);
            switch (choice) {
                case 1: showCurrentMenu(); break;
                case 2: dishManage(); break;
                case 3: arrangeMenu(); break;
                case 4: copyLastWeek(); break;
                case 0: return;
            }
        }
    }

    /** 查看本周食谱 */
    private void showCurrentMenu() {
        List<WeeklyMenu> menus = menuService.getCurrentWeekMenu();
        if (menus.isEmpty()) {
            System.out.println("  本周暂未安排食谱，请先排餐");
            InputUtil.waitForEnter();
            return;
        }
        String[] dayNames = {"", "周一", "周二", "周三", "周四", "周五"};
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  本周食谱");
        System.out.println("══════════════════════════════════════════════════════");
        for (int day = 1; day <= 5; day++) {
            System.out.println("\n  ── " + dayNames[day] + " ──");
            for (WeeklyMenu m : menus) {
                if (m.getDayOfWeek() == day) {
                    System.out.printf("    %s：%s（%s）\n", m.getMealName(), m.getDishName(), getDishTypeName(m.getDishType()));
                }
            }
        }
        System.out.println("\n══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 菜品库管理 */
    private void dishManage() {
        while (true) {
            System.out.println("\n══════ 菜品库管理 ══════");
            System.out.println("  1. 查看所有菜品");
            System.out.println("  2. 按类型查看菜品");
            System.out.println("  3. 添加菜品");
            System.out.println("  4. 修改菜品");
            System.out.println("  5. 删除菜品");
            System.out.println("  0. 返回上级");

            int choice = InputUtil.readInt("请选择：", 0, 5);
            switch (choice) {
                case 1: showAllDishes(); break;
                case 2: showDishesByType(); break;
                case 3: addDish(); break;
                case 4: updateDish(); break;
                case 5: deleteDish(); break;
                case 0: return;
            }
        }
    }

    /** 查看所有菜品 */
    private void showAllDishes() {
        List<Dish> dishes = menuService.getAllDishes();
        printDishTable("所有菜品", dishes);
    }

    /** 按类型查看 */
    private void showDishesByType() {
        System.out.println("  菜品类型：1主食 2荤菜 3素菜 4汤 5水果");
        int type = InputUtil.readInt("  请选择类型：", 1, 5);
        List<Dish> dishes = menuService.getDishesByType(type);
        printDishTable(getDishTypeName(type), dishes);
    }

    /** 添加菜品 */
    private void addDish() {
        Dish dish = new Dish();
        dish.setDishName(InputUtil.readNonEmpty("  菜品名称："));
        System.out.println("  类型：1主食 2荤菜 3素菜 4汤 5水果");
        dish.setDishType(InputUtil.readInt("  请选择类型：", 1, 5));
        String result = menuService.addDish(dish);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 修改菜品 */
    private void updateDish() {
        int id = InputUtil.readInt("  请输入菜品ID：");
        List<Dish> dishes = menuService.getAllDishes();
        Dish dish = dishes.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
        if (dish == null) {
            System.out.println("  ✗ 菜品不存在");
            return;
        }
        dish.setDishName(InputUtil.readString("  菜品名称 [" + dish.getDishName() + "]：", dish.getDishName()));
        System.out.println("  类型：1主食 2荤菜 3素菜 4汤 5水果");
        dish.setDishType(InputUtil.readInt("  类型 [" + dish.getDishType() + "]：", 1, 5));
        String result = menuService.updateDish(dish);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 删除菜品 */
    private void deleteDish() {
        int id = InputUtil.readInt("  请输入菜品ID：");
        if (InputUtil.readConfirm("  确认删除？")) {
            String result = menuService.deleteDish(id);
            System.out.println("  " + (result.contains("已删除") ? "✓" : "✗") + " " + result);
        }
    }

    /** 排餐 */
    private void arrangeMenu() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        System.out.println("  本周周一日期：" + monday);
        System.out.println("  将为周一到周五安排早、中、晚餐");
        System.out.println("  提示：同一天同一餐可添加多道菜\n");

        // 先显示现有菜品库
        showAllDishes();

        String[] dayNames = {"", "周一", "周二", "周三", "周四", "周五"};
        String[] mealNames = {"", "早餐", "午餐", "晚餐"};

        List<WeeklyMenu> menus = new ArrayList<>();
        for (int day = 1; day <= 5; day++) {
            for (int meal = 1; meal <= 3; meal++) {
                System.out.printf("  %s %s - 输入菜品ID（0跳过）：", dayNames[day], mealNames[meal]);
                int dishId = InputUtil.readInt("", 0, 100);
                if (dishId > 0) {
                    WeeklyMenu menu = new WeeklyMenu();
                    menu.setWeekStart(monday);
                    menu.setDayOfWeek(day);
                    menu.setMealType(meal);
                    menu.setDishId(dishId);
                    menus.add(menu);
                }
            }
        }

        if (menus.isEmpty()) {
            System.out.println("  未添加任何食谱");
            return;
        }
        // 清空本周旧食谱，插入新的
        int count = menuService.replaceWeekMenu(monday, menus);
        System.out.println("  ✓ 食谱已保存（共" + count + "条）");
    }

    /** 复制上周食谱 */
    private void copyLastWeek() {
        String result = menuService.copyLastWeekMenu();
        System.out.println("  " + (result.contains("已复制") ? "✓" : "✗") + " " + result);
    }

    /** 打印菜品表格 */
    private void printDishTable(String title, List<Dish> dishes) {
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  %s（共%d道）\n", title, dishes.size());
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s\n",
            InputUtil.padRight("编号", 8), InputUtil.padRight("菜品名称", 14), InputUtil.padRight("类型", 10));
        System.out.println("──────────────────────────────────────────────────────");
        for (Dish d : dishes) {
            System.out.printf("  %s%s%s\n",
                InputUtil.padRight(String.valueOf(d.getId()), 8),
                InputUtil.padRight(d.getDishName(), 14),
                InputUtil.padRight(d.getDishTypeName(), 10));
        }
        System.out.println("══════════════════════════════════════════════════════");
    }

    /** 获取菜品类型名称 */
    private String getDishTypeName(int type) {
        switch (type) {
            case 1: return "主食";
            case 2: return "荤菜";
            case 3: return "素菜";
            case 4: return "汤";
            case 5: return "水果";
            default: return "未知";
        }
    }
}
