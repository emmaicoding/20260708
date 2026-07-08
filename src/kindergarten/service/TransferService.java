package kindergarten.service;

import kindergarten.dao.ChildDao;
import kindergarten.dao.ClassDao;
import kindergarten.dao.TransferLogDao;
import kindergarten.entity.Child;
import kindergarten.entity.ClassInfo;
import kindergarten.entity.TransferLog;
import kindergarten.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 调班业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理幼儿调班操作，包含容量校验和调班日志记录
 */
public class TransferService {
    private final ChildDao childDao = new ChildDao();
    private final ClassDao classDao = new ClassDao();
    private final TransferLogDao transferLogDao = new TransferLogDao();

    /**
     * 执行调班操作
     *
     * @param childId    幼儿ID
     * @param newClassId 目标班级ID
     * @param operatorId 操作人ID
     * @param remark     备注
     * @return 结果描述
     */
    public String transfer(int childId, int newClassId, int operatorId, String remark) {
        // 1. 校验幼儿存在
        Child child = childDao.selectById(childId);
        if (child == null || child.getStatus() != 1) {
            return "幼儿不存在或已离园";
        }
        // 2. 校验目标班级存在
        ClassInfo newClass = classDao.selectById(newClassId);
        if (newClass == null) {
            return "目标班级不存在";
        }
        // 3. 检查是否调到同一班级
        if (child.getClassId().equals(newClassId)) {
            return "该幼儿已在" + newClass.getClassName();
        }
        // 4. 校验目标班级容量
        int currentCount = childDao.countByClassId(newClassId);
        if (currentCount >= newClass.getMaxCount()) {
            return "目标班级「" + newClass.getClassName() + "」人数已满（" + newClass.getMaxCount() + "人）";
        }
        // 5. 获取原班级信息
        ClassInfo oldClass = classDao.selectById(child.getClassId());
        // 6. 构建调班日志
        TransferLog log = new TransferLog();
        log.setChildId(childId);
        log.setOldClassId(child.getClassId());
        log.setNewClassId(newClassId);
        log.setOperatorId(operatorId);
        log.setRemark(remark != null ? remark :
            String.format("%s：%s→%s", child.getName(),
                oldClass != null ? oldClass.getClassName() : "未知",
                newClass.getClassName()));
        // 7. 在同一事务中执行调班 + 记录日志
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            int rows = childDao.updateClass(conn, childId, newClassId);
            if (rows <= 0) {
                conn.rollback();
                return "调班失败";
            }
            transferLogDao.insert(conn, log);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            return "调班失败：" + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }

        return String.format("调班成功：%s 从 %s 调至 %s",
            child.getName(),
            oldClass != null ? oldClass.getClassName() : "未知",
            newClass.getClassName());
    }

    /**
     * 查询所有调班记录
     *
     * @return 调班记录列表
     */
    public List<TransferLog> getAllTransferLogs() {
        return transferLogDao.selectAll();
    }

    /**
     * 查询指定幼儿的调班记录
     *
     * @param childId 幼儿ID
     * @return 调班记录列表
     */
    public List<TransferLog> getChildTransferLogs(int childId) {
        return transferLogDao.selectByChildId(childId);
    }
}
