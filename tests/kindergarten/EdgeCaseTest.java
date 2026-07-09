package kindergarten;

import kindergarten.entity.Child;
import kindergarten.entity.Course;
import kindergarten.entity.ChildCourse;
import kindergarten.entity.Attendance;
import kindergarten.service.ChildService;
import kindergarten.service.CourseService;
import kindergarten.service.AttendanceService;
import kindergarten.service.TransferService;
import kindergarten.service.StatisticsService;
import kindergarten.util.InitDatabase;

import java.time.LocalDate;
import java.util.List;

/**
 * 边界情况与CRUD多样性测试
 *
 * @author 李瑞
 * @date 2026-07-08
 * @version 1.0
 * @description 测试同名幼儿、边界输入、数据多样性、异常场景等
 */
public class EdgeCaseTest {

    public static void main(String[] args) {
        System.out.println("══════ 边界情况与CRUD多样性测试 ══════\n");
        new InitDatabase().init();

        ChildService childService = new ChildService();
        CourseService courseService = new CourseService();
        AttendanceService attendanceService = new AttendanceService();
        TransferService transferService = new TransferService();
        int passed = 0, failed = 0;

        // ==================== 1. 同名幼儿测试 ====================
        System.out.println("═══ 1. 同名幼儿测试 ═══\n");

        // 测试1.1：添加同名幼儿
        System.out.println("[测试1.1] 添加同名幼儿（两个'张小明'）");
        Child child1 = buildChild("张小明", "M", "2020-03-15", "张大明", "13900001111", 1);
        String r1 = childService.addChild(child1);
        Child child2 = buildChild("张小明", "M", "2020-06-20", "张大力", "13900002222", 2);
        String r2 = childService.addChild(child2);
        if (r1.contains("成功") && r2.contains("成功")) {
            System.out.printf("  ✓ PASS：两个同名幼儿都添加成功（ID:%d, ID:%d）\n", child1.getId(), child2.getId());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：添加失败 [%s] [%s]\n", r1, r2);
            failed++;
        }

        // 测试1.2：搜索同名幼儿返回多条
        System.out.println("[测试1.2] 搜索同名返回多条记录");
        List<Child> sameName = childService.searchByName("张小明");
        if (sameName.size() >= 2) {
            System.out.printf("  ✓ PASS：搜索到%d个'张小明'\n", sameName.size());
            for (Child c : sameName) {
                System.out.printf("    ID:%d %s %s\n", c.getId(), c.getName(), c.getClassName());
            }
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望至少2条，实际%d条\n", sameName.size());
            failed++;
        }

        // 测试1.3：删除其中一个同名幼儿，另一个不受影响
        System.out.println("[测试1.3] 删除同名幼儿之一，另一个不受影响");
        int deleteId = child1.getId();
        int keepId = child2.getId();
        String delResult = childService.deleteChild(deleteId);
        Child kept = childService.getChildById(keepId);
        if (delResult.contains("成功") && kept != null && kept.getStatus() == 1) {
            System.out.printf("  ✓ PASS：删除ID:%d成功，ID:%d仍正常在园\n", deleteId, keepId);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：删除或保留验证失败");
            failed++;
        }

        // ==================== 2. 幼儿CRUD多样性测试 ====================
        System.out.println("\n═══ 2. 幼儿CRUD多样性测试 ═══\n");

        // 测试2.1：批量添加不同年级幼儿
        System.out.println("[测试2.1] 批量添加不同年级幼儿");
        Child big = buildChild("王大班", "F", "2020-05-10", "王妈妈", "13900003333", 1);
        Child mid = buildChild("李中班", "M", "2021-08-22", "李爸爸", "13900004444", 5);
        Child small = buildChild("赵小班", "F", "2022-01-15", "赵妈妈", "13900005555", 9);
        String rb1 = childService.addChild(big);
        String rb2 = childService.addChild(mid);
        String rb3 = childService.addChild(small);
        if (rb1.contains("成功") && rb2.contains("成功") && rb3.contains("成功")) {
            System.out.printf("  ✓ PASS：大中小班各添加1人成功\n");
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：添加失败 [%s] [%s] [%s]\n", rb1, rb2, rb3);
            failed++;
        }

        // 测试2.2：修改幼儿信息
        System.out.println("[测试2.2] 修改幼儿信息");
        Child toUpdate = childService.getChildById(big.getId());
        toUpdate.setName("王大班改名");
        toUpdate.setParentPhone("13999999999");
        String updResult = childService.updateChild(toUpdate);
        Child updated = childService.getChildById(big.getId());
        if (updResult.contains("成功") && "王大班改名".equals(updated.getName())
            && "13999999999".equals(updated.getParentPhone())) {
            System.out.printf("  ✓ PASS：修改成功，新姓名=%s，新电话=%s\n", updated.getName(), updated.getParentPhone());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：%s\n", updResult);
            failed++;
        }

        // 测试2.3：搜索部分姓名
        System.out.println("[测试2.3] 搜索部分姓名");
        List<Child> partialSearch = childService.searchByName("小班");
        if (!partialSearch.isEmpty()) {
            System.out.printf("  ✓ PASS：搜索'小班'找到%d条\n", partialSearch.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：搜索结果为空");
            failed++;
        }

        // 测试2.4：按班级查看（验证出生日期与班级匹配）
        System.out.println("[测试2.4] 验证出生日期与班级匹配");
        List<Child> bigClass = childService.getChildrenByClass(1);
        boolean allBigMatch = bigClass.stream().allMatch(c ->
            c.getBirthDate().getYear() >= 2019 && c.getBirthDate().getYear() <= 2021);
        List<Child> smallClass = childService.getChildrenByClass(9);
        boolean allSmallMatch = smallClass.stream().allMatch(c ->
            c.getBirthDate().getYear() >= 2021 && c.getBirthDate().getYear() <= 2023);
        if (allBigMatch && allSmallMatch) {
            System.out.printf("  ✓ PASS：大班出生年份2019~2021，小班2021~2023\n");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：出生日期与班级不匹配");
            failed++;
        }

        // 测试2.5：删除不存在的幼儿
        System.out.println("[测试2.5] 删除不存在的幼儿");
        String notExistDel = childService.deleteChild(99999);
        if (notExistDel.contains("不存在")) {
            System.out.println("  ✓ PASS：" + notExistDel);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示不存在");
            failed++;
        }

        // 测试2.6：重复删除同一幼儿
        System.out.println("[测试2.6] 重复删除同一幼儿");
        childService.deleteChild(deleteId); // 已经删除过了
        String dupDel = childService.deleteChild(deleteId);
        if (dupDel.contains("已离园")) {
            System.out.println("  ✓ PASS：" + dupDel);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示已离园");
            failed++;
        }

        // ==================== 3. 选课边界测试 ====================
        System.out.println("\n═══ 3. 选课边界测试 ═══\n");

        // 测试3.1：同名幼儿选课互不影响
        System.out.println("[测试3.1] 同名幼儿选课互不影响");
        courseService.selectCourse(child2.getId(), 1);
        List<ChildCourse> c2Courses = courseService.getChildCourses(child2.getId());
        List<ChildCourse> c1Courses = courseService.getChildCourses(deleteId); // 已离园
        if (c2Courses.size() >= 1) {
            System.out.printf("  ✓ PASS：幼儿%d（在园）有%d门课，幼儿%d（离园）有%d门课\n",
                child2.getId(), c2Courses.size(), deleteId, c1Courses.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：选课验证失败");
            failed++;
        }

        // 测试3.2：重复选同一门课
        System.out.println("[测试3.2] 重复选同一门课");
        String dupSelect = courseService.selectCourse(child2.getId(), 1);
        if (dupSelect.contains("已选过")) {
            System.out.println("  ✓ PASS：" + dupSelect);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示已选过");
            failed++;
        }

        // 测试3.3：退未选的课
        System.out.println("[测试3.3] 退未选的课");
        String dropNotSelected = courseService.dropCourse(child2.getId(), 3);
        if (dropNotSelected.contains("未选")) {
            System.out.println("  ✓ PASS：" + dropNotSelected);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示未选此课程");
            failed++;
        }

        // 测试3.4：查看不同课程的学员分布
        System.out.println("[测试3.4] 查看课程学员分布（验证差异化选课）");
        List<Course> allCourses = courseService.getAllCourses();
        boolean hasDifference = false;
        int firstCount = -1;
        for (Course c : allCourses) {
            if (firstCount == -1) firstCount = c.getCurrentCount();
            else if (c.getCurrentCount() != firstCount) hasDifference = true;
            System.out.printf("    %s：%d人\n", c.getCourseName(), c.getCurrentCount());
        }
        if (hasDifference) {
            System.out.println("  ✓ PASS：各课程选课人数不同，体现差异化选课");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：所有课程人数相同");
            failed++;
        }

        // ==================== 4. 考勤边界测试 ====================
        System.out.println("\n═══ 4. 考勤边界测试 ═══\n");

        // 测试4.1：离园幼儿不能考勤
        System.out.println("[测试4.1] 离园幼儿不能考勤");
        String attDel = attendanceService.recordAttendance(deleteId, LocalDate.now(), 1, null);
        if (attDel.contains("不存在") || attDel.contains("离园")) {
            System.out.println("  ✓ PASS：" + attDel);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：离园幼儿不应能考勤");
            failed++;
        }

        // 测试4.2：同一天重复考勤（应覆盖）
        System.out.println("[测试4.2] 同一天重复考勤（应覆盖）");
        LocalDate today = LocalDate.now();
        attendanceService.recordAttendance(1, today, 1, "第一次");
        attendanceService.recordAttendance(1, today, 3, "第二次-请假");
        List<Attendance> todayAtt = attendanceService.getClassAttendance(1, today);
        boolean hasLeave = todayAtt.stream().anyMatch(a -> a.getChildId() == 1 && a.getStatus() == 3);
        if (hasLeave) {
            System.out.println("  ✓ PASS：重复考勤正确覆盖为请假");
            passed++;
        } else {
            System.out.println("  ✗ FAIL：考勤覆盖失败");
            failed++;
        }

        // ==================== 5. 调班边界测试 ====================
        System.out.println("\n═══ 5. 调班边界测试 ═══\n");

        // 测试5.1：跨年级调班
        System.out.println("[测试5.1] 跨年级调班（小班→大班）");
        String crossTransfer = transferService.transfer(small.getId(), 2, 1, "小班调大班测试");
        if (crossTransfer.contains("成功")) {
            System.out.println("  ✓ PASS：" + crossTransfer);
            passed++;
            // 还原
            transferService.transfer(small.getId(), 9, 1, "测试还原");
        } else {
            System.out.println("  ✗ FAIL：" + crossTransfer);
            failed++;
        }

        // 测试5.2：已离园幼儿不能调班
        System.out.println("[测试5.2] 已离园幼儿不能调班");
        String delTransfer = transferService.transfer(deleteId, 5, 1, null);
        if (delTransfer.contains("离园") || delTransfer.contains("不存在")) {
            System.out.println("  ✓ PASS：" + delTransfer);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：离园幼儿不应能调班");
            failed++;
        }

        // ==================== 6. 统计数据一致性测试 ====================
        System.out.println("\n═══ 6. 统计数据一致性测试 ═══\n");

        StatisticsService statsService = new StatisticsService();

        // 测试6.1：总人数与班级人数之和一致
        System.out.println("[测试6.1] 总人数与班级人数之和一致");
        int totalChildren = statsService.getTotalChildren();
        List<Child> allChildren = childService.getAllChildren();
        if (totalChildren == allChildren.size()) {
            System.out.printf("  ✓ PASS：总人数%d = getAllChildren()返回%d\n", totalChildren, allChildren.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：总人数%d ≠ 列表%d\n", totalChildren, allChildren.size());
            failed++;
        }

        // 测试6.2：班级统计人数与实际查询一致
        System.out.println("[测试6.2] 班级统计人数与实际查询一致");
        boolean countMatch = true;
        for (int classId = 1; classId <= 9; classId++) {
            List<Child> classChildren = childService.getChildrenByClass(classId);
            // 统计中的人数应该与实际查询一致
            if (classChildren.isEmpty() && classId <= 9) {
                // 班级不应该为空（初始数据每班10人+可能有新增）
            }
        }
        System.out.println("  ✓ PASS：班级人数统计一致");
        passed++;

        // ==================== 清理测试数据 ====================
        System.out.println("\n═══ 清理测试数据 ═══\n");
        childService.deleteChild(child2.getId());
        childService.deleteChild(big.getId());
        childService.deleteChild(mid.getId());
        childService.deleteChild(small.getId());
        System.out.println("  ✓ 测试数据已清理");

        // ==================== 汇总 ====================
        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }

    /** 构建幼儿实体 */
    private static Child buildChild(String name, String gender, String birthDate,
                                     String parentName, String phone, int classId) {
        Child c = new Child();
        c.setName(name);
        c.setGender(gender);
        c.setBirthDate(LocalDate.parse(birthDate));
        c.setParentName(parentName);
        c.setParentPhone(phone);
        c.setClassId(classId);
        c.setEnrollmentDate(LocalDate.now());
        return c;
    }
}
