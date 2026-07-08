package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 班级信息实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_class_info表，存储班级基本信息
 */
public class ClassInfo {
    private Integer id;           // 班级ID
    private String className;     // 班级名称（如：大一班）
    private String grade;         // 年级（大班/中班/小班）
    private Integer maxCount;     // 班级最大人数
    private Integer currentCount; // 当前人数（关联查询用）
    private LocalDateTime createTime; // 创建时间

    public ClassInfo() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Integer getMaxCount() { return maxCount; }
    public void setMaxCount(Integer maxCount) { this.maxCount = maxCount; }

    public Integer getCurrentCount() { return currentCount; }
    public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return String.format("ClassInfo{id=%d, name='%s', grade='%s', count=%d/%d}",
            id, className, grade, currentCount != null ? currentCount : 0, maxCount);
    }
}
