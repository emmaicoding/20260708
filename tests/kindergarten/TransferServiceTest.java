package kindergarten;

import kindergarten.entity.Child;
import kindergarten.entity.TransferLog;
import kindergarten.service.TransferService;
import kindergarten.dao.ChildDao;
import kindergarten.util.InitDatabase;

import java.util.List;

/**
 * TransferService测试
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 测试调班业务逻辑、容量校验、日志记录
 */
public class TransferServiceTest {

    public static void main(String[] args) {
        System.out.println("══════ TransferService测试 ══════\n");
        new InitDatabase().init();

        TransferService service = new TransferService();
        ChildDao childDao = new ChildDao();
        int passed = 0, failed = 0;

        // 测试1：正常调班
        System.out.println("[测试1] 正常调班（幼儿3从大一班调到中一班）");
        String result = service.transfer(3, 4, 1, "测试调班");
        if (result.contains("成功")) {
            System.out.println("  ✓ PASS：" + result);
            // 验证调班结果
            Child child = childDao.selectById(3);
            if (child != null && child.getClassId() == 4) {
                System.out.println("  ✓ 验证：幼儿已调至中一班");
            }
            passed++;
        } else {
            System.out.println("  ✗ FAIL：" + result);
            failed++;
        }

        // 测试2：调到同一班级校验
        System.out.println("[测试2] 调到同一班级校验");
        result = service.transfer(3, 4, 1, null); // 幼儿3刚调到4班
        if (result.contains("已在")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示已在该班级");
            failed++;
        }

        // 测试3：不存在的幼儿
        System.out.println("[测试3] 不存在的幼儿");
        result = service.transfer(9999, 1, 1, null);
        if (result.contains("不存在")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示幼儿不存在");
            failed++;
        }

        // 测试4：不存在的班级
        System.out.println("[测试4] 不存在的目标班级");
        result = service.transfer(1, 99, 1, null);
        if (result.contains("不存在")) {
            System.out.println("  ✓ PASS：" + result);
            passed++;
        } else {
            System.out.println("  ✗ FAIL：应提示班级不存在");
            failed++;
        }

        // 测试5：查看调班记录
        System.out.println("[测试5] 查看调班记录");
        List<TransferLog> logs = service.getAllTransferLogs();
        if (!logs.isEmpty()) {
            System.out.printf("  ✓ PASS：共%d条调班记录\n", logs.size());
            for (TransferLog tl : logs) {
                System.out.printf("    %s：%s→%s（%s）\n",
                    tl.getChildName(), tl.getOldClassName(), tl.getNewClassName(), tl.getRemark());
            }
            passed++;
        } else {
            System.out.println("  ✗ FAIL：调班记录为空");
            failed++;
        }

        // 测试6：查看幼儿调班历史
        System.out.println("[测试6] 查看幼儿调班历史");
        List<TransferLog> childLogs = service.getChildTransferLogs(3);
        if (!childLogs.isEmpty()) {
            System.out.printf("  ✓ PASS：幼儿3共%d条调班记录\n", childLogs.size());
            passed++;
        } else {
            System.out.println("  ✗ FAIL：幼儿调班记录为空");
            failed++;
        }

        // 还原：将幼儿3调回大一班
        service.transfer(3, 1, 1, "测试还原");

        System.out.println("\n══════════════════════════════════");
        System.out.printf("  测试结果：%d 通过 / %d 失败\n", passed, failed);
        System.out.println("══════════════════════════════════");
    }
}
