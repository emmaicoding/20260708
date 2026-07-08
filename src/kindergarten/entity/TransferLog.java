package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 调班记录实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_transfer_log表，记录幼儿调班操作日志
 */
public class TransferLog {
    private Integer id;              // 记录ID
    private Integer childId;         // 幼儿ID
    private String childName;        // 幼儿姓名（关联查询用）
    private Integer oldClassId;      // 原班级ID
    private String oldClassName;     // 原班级名称（关联查询用）
    private Integer newClassId;      // 新班级ID
    private String newClassName;     // 新班级名称（关联查询用）
    private Integer operatorId;      // 操作人ID
    private String operatorName;     // 操作人姓名（关联查询用）
    private LocalDateTime transferDate; // 调班时间
    private String remark;           // 备注

    public TransferLog() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public Integer getOldClassId() { return oldClassId; }
    public void setOldClassId(Integer oldClassId) { this.oldClassId = oldClassId; }

    public String getOldClassName() { return oldClassName; }
    public void setOldClassName(String oldClassName) { this.oldClassName = oldClassName; }

    public Integer getNewClassId() { return newClassId; }
    public void setNewClassId(Integer newClassId) { this.newClassId = newClassId; }

    public String getNewClassName() { return newClassName; }
    public void setNewClassName(String newClassName) { this.newClassName = newClassName; }

    public Integer getOperatorId() { return operatorId; }
    public void setOperatorId(Integer operatorId) { this.operatorId = operatorId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public LocalDateTime getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDateTime transferDate) { this.transferDate = transferDate; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString() {
        return String.format("TransferLog{child='%s', %s→%s, by='%s', time=%s}",
            childName, oldClassName, newClassName, operatorName, transferDate);
    }
}
