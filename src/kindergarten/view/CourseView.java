package kindergarten.view;

import kindergarten.entity.Course;
import kindergarten.entity.ChildCourse;
import kindergarten.entity.Child;
import kindergarten.service.CourseService;
import kindergarten.service.ChildService;
import kindergarten.util.InputUtil;

import java.util.List;

/**
 * 课程管理视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，支持课程增删改查、选课退课等
 */
public class CourseView {
    private final CourseService courseService = new CourseService();
    private final ChildService childService = new ChildService();

    /**
     * 显示课程管理菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n══════ 课程管理 ══════");
            System.out.println("  1. 查看所有课程");
            System.out.println("  2. 添加课程");
            System.out.println("  3. 修改课程信息");
            System.out.println("  4. 删除课程");
            System.out.println("  5. 为幼儿选课");
            System.out.println("  6. 为幼儿退课");
            System.out.println("  7. 查看幼儿已选课程");
            System.out.println("  8. 查看课程学员名单");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 8);
            switch (choice) {
                case 1: showAllCourses(); break;
                case 2: addCourse(); break;
                case 3: updateCourse(); break;
                case 4: deleteCourse(); break;
                case 5: selectCourse(); break;
                case 6: dropCourse(); break;
                case 7: showChildCourses(); break;
                case 8: showCourseStudents(); break;
                case 0: return;
            }
        }
    }

    /** 查看所有课程 */
    private void showAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  所有课程");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s%s%s\n",
            InputUtil.padRight("编号", 8), InputUtil.padRight("课程名称", 14), InputUtil.padRight("已选人数", 12),
            InputUtil.padRight("容量上限", 12), InputUtil.padRight("课程描述", 20));
        System.out.println("──────────────────────────────────────────────────────");
        for (Course c : courses) {
            System.out.printf("  %s%s%s%s%s\n",
                InputUtil.padRight(String.valueOf(c.getId()), 8),
                InputUtil.padRight(c.getCourseName(), 14),
                InputUtil.padRight(String.valueOf(c.getCurrentCount()), 12),
                InputUtil.padRight(String.valueOf(c.getMaxCount()), 12),
                InputUtil.padRight(c.getDescription() != null ? c.getDescription() : "", 20));
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }

    /** 添加课程 */
    private void addCourse() {
        System.out.println("\n══════ 添加课程 ══════");
        Course course = new Course();
        course.setCourseName(InputUtil.readNonEmpty("  课程名称："));
        course.setMaxCount(InputUtil.readInt("  容量上限（推荐15）：", 1, 100));
        course.setDescription(InputUtil.readString("  课程描述（可选）：", ""));
        String result = courseService.addCourse(course);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 修改课程信息 */
    private void updateCourse() {
        int id = InputUtil.readInt("  请输入课程ID：");
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("  ✗ 课程不存在");
            return;
        }
        System.out.println("  当前信息：" + course.getCourseName() + " | 容量" + course.getMaxCount());
        course.setCourseName(InputUtil.readString("  课程名称 [" + course.getCourseName() + "]：", course.getCourseName()));
        course.setMaxCount(InputUtil.readInt("  容量上限 [" + course.getMaxCount() + "]：", 1, 100));
        course.setDescription(InputUtil.readString("  课程描述：", course.getDescription()));
        String result = courseService.updateCourse(course);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 删除课程 */
    private void deleteCourse() {
        int id = InputUtil.readInt("  请输入课程ID：");
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("  ✗ 课程不存在");
            return;
        }
        System.out.println("  确认删除课程：" + course.getCourseName() + "（当前" + course.getCurrentCount() + "人选课）");
        if (InputUtil.readConfirm("  确认删除？")) {
            String result = courseService.deleteCourse(id);
            System.out.println("  " + (result.contains("成功") || result.contains("已删除") ? "✓" : "✗") + " " + result);
        }
    }

    /** 为幼儿选课 */
    private void selectCourse() {
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        Child child = childService.getChildById(childId);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        // 显示已选课程
        List<ChildCourse> existing = courseService.getChildCourses(childId);
        System.out.println("  " + child.getName() + " 已选" + existing.size() + "门课程");
        for (ChildCourse cc : existing) {
            System.out.println("    ✓ " + cc.getCourseName());
        }
        // 显示可选课程
        System.out.println("\n  可选课程：");
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            boolean selected = existing.stream().anyMatch(cc -> cc.getCourseId().equals(c.getId()));
            String mark = selected ? " [已选]" : (c.getCurrentCount() >= c.getMaxCount() ? " [满员]" : "");
            System.out.printf("    %d. %s（%d/%d人）%s\n",
                c.getId(), c.getCourseName(), c.getCurrentCount(), c.getMaxCount(), mark);
        }
        int courseId = InputUtil.readInt("  请选择课程ID：");
        String result = courseService.selectCourse(childId, courseId);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 为幼儿退课 */
    private void dropCourse() {
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        List<ChildCourse> courses = courseService.getChildCourses(childId);
        if (courses.isEmpty()) {
            System.out.println("  该幼儿暂无选课记录");
            return;
        }
        System.out.println("  已选课程：");
        for (ChildCourse cc : courses) {
            System.out.printf("    %d. %s\n", cc.getCourseId(), cc.getCourseName());
        }
        int courseId = InputUtil.readInt("  请输入要退的课程ID：");
        String result = courseService.dropCourse(childId, courseId);
        System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
    }

    /** 查看幼儿已选课程 */
    private void showChildCourses() {
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        Child child = childService.getChildById(childId);
        if (child == null) {
            System.out.println("  ✗ 幼儿不存在");
            return;
        }
        List<ChildCourse> courses = courseService.getChildCourses(childId);
        System.out.println("\n  " + child.getName() + "（" + child.getClassName() + "）的课程：");
        if (courses.isEmpty()) {
            System.out.println("    暂未选课");
        } else {
            for (int i = 0; i < courses.size(); i++) {
                System.out.printf("    %d. %s\n", i + 1, courses.get(i).getCourseName());
            }
        }
        System.out.println("  已选" + courses.size() + "/4门课程");
        InputUtil.waitForEnter();
    }

    /** 查看课程学员名单 */
    private void showCourseStudents() {
        List<Course> courses = courseService.getAllCourses();
        System.out.println("  可选课程：");
        for (Course c : courses) {
            System.out.printf("    %d. %s（%d/%d人）\n", c.getId(), c.getCourseName(), c.getCurrentCount(), c.getMaxCount());
        }
        int courseId = InputUtil.readInt("  请输入课程ID：");
        List<ChildCourse> students = courseService.getCourseStudents(courseId);
        Course course = courseService.getCourseById(courseId);
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.printf("  「%s」学员名单（共%d人）\n",
            course != null ? course.getCourseName() : "", students.size());
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  %s%s\n", InputUtil.padRight("幼儿ID", 10), InputUtil.padRight("姓名", 12));
        System.out.println("──────────────────────────────────────────────────────");
        for (ChildCourse cc : students) {
            System.out.printf("  %s%s\n",
                InputUtil.padRight(String.valueOf(cc.getChildId()), 10),
                InputUtil.padRight(cc.getChildName(), 12));
        }
        System.out.println("══════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }
}
