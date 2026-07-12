package kindergarten.view;

import kindergarten.entity.Child;
import kindergarten.entity.ClassInfo;
import kindergarten.entity.TransferLog;
import kindergarten.entity.User;
import kindergarten.service.TransferService;
import kindergarten.service.ChildService;
import kindergarten.dao.ClassDao;
import kindergarten.util.InputUtil;

import java.util.List;

/**
 * 调班管理视图
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 管理员操作界面，支持跨年级调班和查看调班历史
 */
public class TransferView {
    private final TransferService transferService = new TransferService();
    private final ChildService childService = new ChildService();
    private final ClassDao classDao = new ClassDao();

    /**
     * 显示调班管理菜单
     *
     * @param user 当前登录用户（记录操作人）
     */
    public void show(User user) {
        while (true) {
            System.out.println("\n══════ 调班管理 ══════");
            System.out.println("  1. 执行调班");
            System.out.println("  2. 查看调班记录");
            System.out.println("  3. 查看幼儿调班历史");
            System.out.println("  0. 返回上级菜单");

            int choice = InputUtil.readInt("请选择：", 0, 3);
            switch (choice) {
                case 1: doTransfer(user); break;
                case 2: showAllLogs(); break;
                case 3: showChildLogs(); break;
                case 0: return;
            }
        }
    }

    /** 执行调班 */
    private void doTransfer(User user) {
        System.out.println("\n══════ 执行调班 ══════");

        // 显示幼儿信息
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        Child child = childService.getChildById(childId);
        if (child == null || child.getStatus() != 1) {
            System.out.println("  ✗ 幼儿不存在或已离园");
            return;
        }
        System.out.printf("  幼儿：%s（%s）\n", child.getName(), child.getClassName());

        // 显示可选班级
        List<ClassInfo> classes = classDao.selectAll();
        System.out.println("\n  可选班级：");
        for (ClassInfo cls : classes) {
            String current = cls.getId().equals(child.getClassId()) ? " [当前]" : "";
            System.out.printf("    %d. %s（%s）%d/%d人%s\n",
                cls.getId(), cls.getClassName(), cls.getGrade(),
                cls.getCurrentCount(), cls.getMaxCount(), current);
        }

        int newClassId = InputUtil.readInt("  请输入目标班级ID：", 1, 9);
        String remark = InputUtil.readString("  备注（可选）：", "");

        System.out.printf("\n  确认将 %s 从 %s 调至",
            child.getName(), child.getClassName());
        ClassInfo target = classes.stream().filter(c -> c.getId().equals(newClassId)).findFirst().orElse(null);
        System.out.printf(" %s？\n", target != null ? target.getClassName() : "");

        if (InputUtil.readConfirm("  确认调班？")) {
            String result = transferService.transfer(childId, newClassId, user.getId(), remark);
            System.out.println("  " + (result.contains("成功") ? "✓" : "✗") + " " + result);
        } else {
            System.out.println("  已取消");
        }
    }

    /** 查看所有调班记录 */
    private void showAllLogs() {
        List<TransferLog> logs = transferService.getAllTransferLogs();
        printTransferLogs("所有调班记录", logs);
    }

    /** 查看幼儿调班历史 */
    private void showChildLogs() {
        int childId = InputUtil.readInt("  请输入幼儿ID：");
        List<TransferLog> logs = transferService.getChildTransferLogs(childId);
        Child child = childService.getChildById(childId);
        String title = child != null ? child.getName() + " 的调班历史" : "幼儿调班历史";
        printTransferLogs(title, logs);
    }

    /** 打印调班记录表格 */
    private void printTransferLogs(String title, List<TransferLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("  暂无调班记录");
            InputUtil.waitForEnter();
            return;
        }
        System.out.println("\n══════════════════════════════════════════════════════════════════════");
        System.out.printf("  %s（共%d条）\n", title, logs.size());
        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.printf("  %s%s%s%s%s%s%s\n",
            InputUtil.padRight("编号", 8), InputUtil.padRight("幼儿", 12), InputUtil.padRight("原班级", 12),
            InputUtil.padRight("新班级", 12), InputUtil.padRight("操作人", 12), InputUtil.padRight("时间", 22), InputUtil.padRight("备注", 12));
        System.out.println("──────────────────────────────────────────────────────────────────────────");
        for (TransferLog tl : logs) {
            System.out.printf("  %s%s%s%s%s%s%s\n",
                InputUtil.padRight(String.valueOf(tl.getId()), 8),
                InputUtil.padRight(tl.getChildName(), 12),
                InputUtil.padRight(tl.getOldClassName(), 12),
                InputUtil.padRight(tl.getNewClassName(), 12),
                InputUtil.padRight(tl.getOperatorName(), 12),
                InputUtil.padRight(String.valueOf(tl.getTransferDate()), 22),
                InputUtil.padRight(tl.getRemark() != null ? tl.getRemark() : "", 12));
        }
        System.out.println("══════════════════════════════════════════════════════════════════════");
        InputUtil.waitForEnter();
    }
}
