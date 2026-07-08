package kindergarten;

import kindergarten.entity.Child;
import kindergarten.service.ChildService;
import kindergarten.util.InitDatabase;

import java.time.LocalDate;
import java.util.List;

/**
 * ChildService测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试幼儿业务逻辑层的增删改查和数据校验
 */
public class ChildServiceTest {

    public static void main(String[] args) {
        System.out.println("══════ ChildService测试 ══════\n");
        new InitDatabase().init();

        ChildService service = new ChildService();
        int passed = 0, failed = 0;

        // 测试1：正常添加幼儿
        System.out.println("[测试1] 正常添加幼儿");
        Child child = new Child();
        child.setName("业务测试幼儿");
        child.setGender("F");
        child.setBirthDate(LocalDate.of(2021, 3, 10));
        child.setParentName("业务测试家长");
        child.setParentPhone("13900009999");
        child.setClassId(2); // 大二班
        child.setEnrollmentDate(LocalDate.now());
        String result = service.addChild(child);
        if (result.contains("成功")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        // 测试2：姓名为空校验
        System.out.println("[测试2] 姓名为空校验");
        Child emptyName = new Child();
        emptyName.setName("");
        emptyName.setClassId(1);
        result = service.addChild(emptyName);
        if (result.contains("姓名不能为空")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示姓名不能为空");
            failed++;
        }

        // 测试3：搜索幼儿
        System.out.println("[测试3] 按姓名搜索");
        List<Child> found = service.searchByName("业务测试");
        if (!found.isEmpty()) {
            System.out.printf("  ✓ PASS：搜索到%d名幼儿\n", found.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：搜索结果为空");
            failed++;
        }

        // 测试4：查询幼儿详情
        System.out.println("[测试4] 查询幼儿详情");
        Child detail = service.getChildById(1);
        if (detail != null && detail.getClassName() != null) {
            System.out.printf("  ✓ PASS：%s（%s）\n", detail.getName(), detail.getClassName());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：查询失败");
            failed++;
        }

        // 测试5：修改幼儿信息
        System.out.println("[测试5] 修改幼儿信息");
        if (!found.isEmpty()) {
            Child toUpdate = found.get(0);
            toUpdate.setName("修改后的名字");
            result = service.updateChild(toUpdate);
            if (result.contains("成功")) {
                System.out.println("  ✓ PASS：" + result);
                passed++;
            } else {
                System.out.println("  ✗ FAIL：" + result);
                failed++;
            }
        }

        // 测试6：删除幼儿
        System.out.println("[测试6] 删除幼儿（标记离园）");
        if (!found.isEmpty()) {
            int id = found.get(0).getId();
            result = service.deleteChild(id);
            if (result.contains("成功")) {
                System.out.println("  ✓ PASS：" + result);
                passed++;
            } else {
                System.out.println("  ✗ FAIL：" + result);
                failed++;
            }
        }

        // 测试7：重复删除
        System.out.println("[测试7] 重复删除校验");
        if (!found.isEmpty()) {
            int id = found.get(0).getId();
            result = service.deleteChild(id);
            if (result.contains("已离园")) {
                System.out.println("  ✓ PASS：" + result);
                passed++;
            } else {
                System.out.println("  ✗ FAIL：应提示已离园");
                failed++;
            }
        }

        // 测试8：按班级查询
        System.out.println("[测试8] 按班级查询幼儿");
        List<Child> classChildren = service.getChildrenByClass(1);
        if (classChildren.size() == 10) {
            System.out.printf("  ✓ PASS：大一班%d人\n", classChildren.size());
            passed++;
        } else {
            System.out.printf("  ✗ FAIL：期望10，实际%d\n", classChildren.size());
            failed++;
        }

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
