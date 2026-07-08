package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 兴趣课程实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_course表，存储兴趣课程信息
 */
public class Course {
    private Integer id;              // 课程ID
    private String courseName;       // 课程名称
    private Integer maxCount;        // 容量上限
    private String description;      // 课程描述
    private Integer currentCount;    // 当前选课人数（关联查询用）
    private LocalDateTime createTime;// 创建时间

    public Course() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getMaxCount() { return maxCount; }
    public void setMaxCount(Integer maxCount) { this.maxCount = maxCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCurrentCount() { return currentCount; }
    public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return String.format("Course{id=%d, name='%s', count=%d/%d}",
            id, courseName, currentCount != null ? currentCount : 0, maxCount);
    }
}
