package kindergarten.view;

import kindergarten.entity.Child;
import kindergarten.entity.ClassInfo;
import kindergarten.entity.ChildCourse;
import kindergarten.service.ChildService;
import kindergarten.service.CourseService;
import kindergarten.service.ClassService;
import kindergarten.util.InputUtil;

import java.util.List;

/**
 * 幼儿学籍管理视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，支持幼儿增删改查、按班级查看等
 */
public class ChildView {
    private final ChildService childService = new ChildService();
    private final CourseService courseService = new CourseService();
    private final ClassService classService = new ClassService();

    /**
     * 显示幼儿学籍管理菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n══════ 幼儿学籍管理 ══════");
            System.out.println("  1. 查看所有幼儿");
            System.out.println("  2. 按班级查看幼儿");
            System.out.println("  3. 按姓名搜索幼儿");
            System.out.println("  4. 查看幼儿详情");
            System.out.println("  5. 添加幼儿");
            System.out.println("  6. 修改幼儿信息");
            System.out.println("  7. 删除幼儿（标记离园）");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 7);
            switch (choice) {
                case 1: showAllChildren(); break;
                case 2: showChildrenByClass(); break;
                case 3: searchChild(); break;
                case 4: showChildDetail(); break;
                case 5: addChild(); break;
                case 6: updateChild(); break;
                case 7: deleteChild(); break;
                case 0: return;
            }
        }
    }

    /** 查看所有幼儿（分页） */
    private void showAllChildren() {
        List<Child> children = childService.getAllChildren();
        if (children.isEmpty()) {
            System.out.println("  暂无幼儿信息");
            return;
        }
        printChildTable("所有在园幼儿", children);
    }

    /** 按班级查看幼儿 */
    private void showChildrenByClass() {
        List<ClassInfo> classes = classService.getAllClasses();
        System.out.println("\n  可选班级：");
        for (ClassInfo cls : classes) {
            System.out.printf("    %d. %s（%s）- %d人\n",
                cls.getId(), cls.getClassName(), cls.getGrade(), cls.getCurrentCount());
        }
        System.out.println("  提示：输入班级编号（1-9）或班级名称（如\"大一班\"）");
        String input = InputUtil.readNonEmpty("  请输入班级：");

        int classId = -1;
        String title = "班级幼儿名单";

        // 尝试解析为数字
        try {
            classId = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            // 不是数字，按班级名称匹配
            for (ClassInfo cls : classes) {
                if (cls.getClassName().contains(input.trim()) || input.trim().contains(cls.getClassName())) {
                    classId = cls.getId();
                    title = cls.getClassName() + " 幼儿名单";
                    break;
                }
            }
        }

        if (classId < 1 || classId > 9) {
            System.out.println("  ✗ 未找到匹配的班级");
            return;
        }

        // 如果是数字输入，获取班级名称
        if (title.equals("班级幼儿名单")) {
            final int targetClassId = classId;
            ClassInfo cls = classes.stream().filter(c -> c.getId().equals(targetClassId)).findFirst().orElse(null);
            title = cls != null ? cls.getClassName() + " 幼儿名单" : "班级幼儿名单";
        }

        List<Child> children = childService.getChildrenByClass(classId);
        printChildTable(title, children);
    }

    /** 按姓名搜索 */
    private void searchChild() {
        String name = InputUtil.readNonEmpty("  请输入幼儿姓名关键字：");
        List<Child> children = childService.searchByName(name);
        if (children.isEmpty()) {
            System.out.println("  未找到匹配的幼儿");
            return;
        }
        printChildTable("搜索结果", children);
    }

    /** 查看幼儿详情（含所学课程） */
    private void showChildDetail() {
        int id = InputUtil.readInt("  请输入幼儿ID：");
        Child child = childService.getChildById(id);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        System.out.println("\n══════ 幼儿详情 ══════");
        System.out.printf("  编号：%d\n", child.getId());
        System.out.printf("  姓名：%s\n", child.getName());
        System.out.printf("  性别：%s\n", child.getGenderName());
        System.out.printf("  出生日期：%s\n", child.getBirthDate());
        System.out.printf("  家长：%s\n", child.getParentName());
        System.out.printf("  电话：%s\n", child.getParentPhone());
        System.out.printf("  班级：%s\n", child.getClassName());
        System.out.printf("  入园日期：%s\n", child.getEnrollmentDate());
        System.out.printf("  状态：%s\n", child.getStatusName());

        // 显示所学课程
        List<ChildCourse> courses = courseService.getChildCourses(child.getId());
        System.out.println("\n  所学课程：");
        if (courses.isEmpty()) {
            System.out.println("    暂未选课");
        } else {
            for (ChildCourse cc : courses) {
                System.out.printf("    - %s\n", cc.getCourseName());
            }
        }
        System.out.println("════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 添加幼儿 */
    private void addChild() {
        System.out.println("\n══════ 添加幼儿 ══════");
        Child child = new Child();
        child.setName(InputUtil.readNonEmpty("  姓名："));
        child.setGender(InputUtil.readGender("  性别"));
        child.setBirthDate(InputUtil.readDate("  出生日期"));
        child.setParentName(InputUtil.readNonEmpty("  家长姓名："));
        child.setParentPhone(InputUtil.readPhone("  家长电话："));

        // 显示可选班级
        List<ClassInfo> classes = classService.getAllClasses();
        System.out.println("  可选班级：");
        for (ClassInfo cls : classes) {
            System.out.printf("    %d. %s（%d/%d人）\n",
                cls.getId(), cls.getClassName(), cls.getCurrentCount(), cls.getMaxCount());
        }
        child.setClassId(InputUtil.readInt("  请选择班级ID：", 1, 9));
        child.setEnrollmentDate(java.time.LocalDate.now());

        String result = childService.addChild(child);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 修改幼儿信息 */
    private void updateChild() {
        int id = InputUtil.readInt("  请输入要修改的幼儿ID：");
        Child child = childService.getChildById(id);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        System.out.println("  当前信息：" + child.getName() + " | " + child.getGenderName() +
            " | " + child.getBirthDate() + " | " + child.getParentName() + " | " + child.getParentPhone());
        System.out.println("  （直接回车保持原值）");

        String name = InputUtil.readString("  姓名 [" + child.getName() + "]：", child.getName());
        String gender = InputUtil.readString("  性别(M/F) [" + child.getGender() + "]：", child.getGender());
        String parentName = InputUtil.readString("  家长姓名 [" + child.getParentName() + "]：", child.getParentName());
        String parentPhone = InputUtil.readString("  家长电话 [" + child.getParentPhone() + "]：", child.getParentPhone());

        child.setName(name);
        child.setGender(gender.toUpperCase());
        child.setParentName(parentName);
        child.setParentPhone(parentPhone);

        String result = childService.updateChild(child);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 删除幼儿（标记离园） */
    private void deleteChild() {
        int id = InputUtil.readInt("  请输入要删除的幼儿ID：");
        Child child = childService.getChildById(id);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        System.out.println("  确认删除幼儿：" + child.getName() + "（" + child.getClassName() + "）");
        System.out.println("  选课和考勤记录将保留，历史可追溯");
        if (InputUtil.readConfirm("  确认删除？")) {
            String result = childService.deleteChild(id);
            System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
        } else {
            System.out.println("  已取消");
        }
    }

    /** 打印幼儿表格 */
    private void printChildTable(String title, List<Child> children) {
        System.out.println("\n════════════════════════════════════════════════════════════════");
        System.out.printf("  %s（共%d人）\n", title, children.size());
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s%s%s%s%s\n",
            InputUtil.padRight("编号", 8), InputUtil.padRight("姓名", 12), InputUtil.padRight("性别", 8),
            InputUtil.padRight("出生日期", 16), InputUtil.padRight("班级", 14), InputUtil.padRight("家长", 12), InputUtil.padRight("联系电话", 16));
        System.out.println("──────────────────────────────────────────────────────────────────");
        for (Child c : children) {
            System.out.printf("  %s%s%s%s%s%s%s\n",
                InputUtil.padRight(String.valueOf(c.getId()), 8),
                InputUtil.padRight(c.getName(), 12),
                InputUtil.padRight(c.getGenderName(), 8),
                InputUtil.padRight(String.valueOf(c.getBirthDate()), 16),
                InputUtil.padRight(c.getClassName(), 14),
                InputUtil.padRight(c.getParentName(), 12),
                InputUtil.padRight(c.getParentPhone(), 16));
        }
        System.out.println("════════════════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }
}
