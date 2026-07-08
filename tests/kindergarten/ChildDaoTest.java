package kindergarten;

import kindergarten.dao.ChildDao;
import kindergarten.entity.Child;
import kindergarten.util.InitDatabase;

import java.time.LocalDate;
import java.util.List;

/**
 * ChildDao测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试幼儿数据访问层的增删改查操作
 */
public class ChildDaoTest {

    public static void main(String[] args) {
        System.out.println("══════ ChildDao测试 ══════\n");
        new InitDatabase().init();

        ChildDao dao = new ChildDao();
        int passed = 0, failed = 0;

        // 测试1：查询所有幼儿
        System.out.println("[测试1] 查询所有在园幼儿");
        List<Child> all = dao.selectAll();
        if (all.size() == 90) {
            System.out.printf("  ✓ PASS：共%d名在园幼儿\n", all.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望90，实际%d\n", all.size());
            failed++;
        }

        // 测试2：根据ID查询
        System.out.println("[测试2] 根据ID查询幼儿");
        Child child = dao.selectById(1);
        if (child != null && child.getClassName() != null) {
            System.out.printf("  ✓ PASS：%s（%s）\n", child.getName(), child.getClassName());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：查询失败");
            failed++;
        }

        // 测试3：按班级查询
        System.out.println("[测试3] 按班级查询幼儿");
        List<Child> class1 = dao.selectByClassId(1);
        if (class1.size() == 10) {
            System.out.printf("  ✓ PASS：大一班共%d名幼儿\n", class1.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望10，实际%d\n", class1.size());
            failed++;
        }

        // 测试4：按姓名搜索
        System.out.println("[测试4] 按姓名搜索");
        List<Child> searchResult = dao.selectByName("张");
        if (!searchResult.isEmpty()) {
            System.out.printf("  ✓ PASS：搜索到%d名幼儿\n", searchResult.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：搜索结果为空");
            failed++;
        }

        // 测试5：添加幼儿
        System.out.println("[测试5] 添加幼儿");
        Child newChild = new Child();
        newChild.setName("测试幼儿");
        newChild.setGender("M");
        newChild.setBirthDate(LocalDate.of(2021, 6, 15));
        newChild.setParentName("测试家长");
        newChild.setParentPhone("13900000001");
        newChild.setClassId(1);
        newChild.setEnrollmentDate(LocalDate.now());
        int newId = dao.insert(newChild);
        if (newId > 0) {
            System.out.printf("  ✓ PASS：添加成功，ID=%d\n", newId);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：添加失败");
            failed++;
        }

        // 测试6：修改幼儿信息
        System.out.println("[测试6] 修改幼儿信息");
        if (newId > 0) {
            newChild.setId(newId);
            newChild.setName("测试幼儿改名");
            int rows = dao.update(newChild);
            if (rows > 0) {
                Child updated = dao.selectById(newId);
                if ("测试幼儿改名".equals(updated.getName())) {
                    System.out.println("  ✓ PASS：修改成功");
                    passed++;
                } else {
                    System.out.println("  ✗ FAIL：修改后名字不匹配");
                    failed++;
                }
            } else {
                System.out.println("  ✗ FAIL：修改失败");
                failed++;
            }
        }

        // 测试7：统计班级人数
        System.out.println("[测试7] 统计班级人数");
        int count = dao.countByClassId(1);
        if (count >= 10) {
            System.out.printf("  ✓ PASS：大一班%d人\n", count);
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：人数异常%d\n", count);
            failed++;
        }

        // 测试8：软删除幼儿
        System.out.println("[测试8] 软删除幼儿");
        if (newId > 0) {
            int rows = dao.delete(newId);
            Child deleted = dao.selectById(newId);
            if (rows > 0 && deleted.getStatus() == 0) {
                System.out.println("  ✓ PASS：删除成功（status=0）");
                passed++;
            } else {
                System.out.println("  ✗ FAIL：删除失败");
                failed++;
            }
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
