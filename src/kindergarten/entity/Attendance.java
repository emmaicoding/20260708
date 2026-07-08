package kindergarten.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_attendance表，记录幼儿每日考勤状态
 */
public class Attendance {
    private Integer id;              // 记录ID
    private Integer childId;         // 幼儿ID
    private String childName;        // 幼儿姓名（关联查询用）
    private LocalDate attendDate;    // 考勤日期
    private Integer status;          // 状态：1出勤 2缺勤 3请假 4迟到
    private String remark;           // 备注
    private LocalDateTime createTime;// 记录时间

    public Attendance() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public LocalDate getAttendDate() { return attendDate; }
    public void setAttendDate(LocalDate attendDate) { this.attendDate = attendDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /** 获取考勤状态中文名 */
    public String getStatusName() {
        switch (status) {
            case 1: return "出勤";
            case 2: return "缺勤";
            case 3: return "请假";
            case 4: return "迟到";
            default: return "未知";
        }
    }

    @Override
    public String toString() {
        return String.format("Attendance{child='%s', date=%s, status='%s'}",
            childName, attendDate, getStatusName());
    }
}
