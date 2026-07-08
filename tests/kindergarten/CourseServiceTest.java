package kindergarten;

import kindergarten.entity.Course;
import kindergarten.entity.ChildCourse;
import kindergarten.service.CourseService;
import kindergarten.util.InitDatabase;

import java.util.List;

/**
 * CourseService测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试课程管理、选课、退课等业务逻辑
 */
public class CourseServiceTest {

    public static void main(String[] args) {
        System.out.println("══════ CourseService测试 ══════\n");
        new InitDatabase().init();

        CourseService service = new CourseService();
        int passed = 0, failed = 0;

        // 测试1：查询所有课程
        System.out.println("[测试1] 查询所有课程");
        List<Course> courses = service.getAllCourses();
        if (courses.size() >= 4) {
            System.out.printf("  ✓ PASS：共%d门课程\n", courses.size());
            for (Course c : courses) {
                System.out.printf("    %s（%d/%d人）\n", c.getCourseName(), c.getCurrentCount(), c.getMaxCount());
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望至少4门，实际%d门\n", courses.size());
            failed++;
        }

        // 测试2：添加新课程
        System.out.println("[测试2] 添加新课程");
        Course newCourse = new Course();
        newCourse.setCourseName("书法");
        newCourse.setMaxCount(10);
        newCourse.setDescription("书法兴趣课");
        String result = service.addCourse(newCourse);
        if (result.contains("成功")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        // 测试3：幼儿选课（幼儿1已有4门课）
        System.out.println("[测试3] 选课数量限制校验");
        result = service.selectCourse(1, 5); // 幼儿1已选4门，再选应失败
        if (result.contains("已选满")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示已选满");
            failed++;
        }

        // 测试4：退课后再选课
        System.out.println("[测试4] 退课后再选课");
        result = service.dropCourse(1, 4); // 退掉美术
        if (result.contains("成功")) {
            System.out.println("  ✓ 退课成功");
            result = service.selectCourse(1, 5); // 选书法
            if (result.contains("成功")) {
                System.out.println("  ✓ PASS：退课后选课成功");
                passed++;
            } else {
                System.out.println("  ✗ FAIL：" + result);
                failed++;
            }
            // 还原：退书法，选回美术
            service.dropCourse(1, 5);
            service.selectCourse(1, 4);
        } else {
            System.out.println("  ✗ FAIL：退课失败");
            failed++;
        }

        // 测试5：查询幼儿课程
        System.out.println("[测试5] 查询幼儿已选课程");
        List<ChildCourse> childCourses = service.getChildCourses(1);
        if (childCourses.size() == 4) {
            System.out.printf("  ✓ PASS：幼儿1已选%d门课程\n", childCourses.size());
            for (ChildCourse cc : childCourses) {
                System.out.printf("    %s\n", cc.getCourseName());
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望4门，实际%d门\n", childCourses.size());
            failed++;
        }

        // 测试6：查看课程学员
        System.out.println("[测试6] 查看课程学员名单");
        List<ChildCourse> students = service.getCourseStudents(1); // 舞蹈
        if (students.size() > 0) {
            System.out.printf("  ✓ PASS：舞蹈课共%d名学员\n", students.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：学员列表为空");
            failed++;
        }

        // 测试7：修改课程
        System.out.println("[测试7] 修改课程信息");
        List<Course> allCourses = service.getAllCourses();
        Course toUpdate = allCourses.get(allCourses.size() - 1); // 最后一门（书法）
        toUpdate.setMaxCount(20);
        result = service.updateCourse(toUpdate);
        if (result.contains("成功")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        // 测试8：删除课程
        System.out.println("[测试8] 删除无学员课程");
        result = service.deleteCourse(toUpdate.getId());
        if (result.contains("已删除")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
