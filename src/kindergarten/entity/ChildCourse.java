package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 幼儿选课关系实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_child_course表，记录幼儿与课程的多对多关系
 */
public class ChildCourse {
    private Integer id;              // 记录ID
    private Integer childId;         // 幼儿ID
    private Integer courseId;        // 课程ID
    private String childName;        // 幼儿姓名（关联查询用）
    private String courseName;       // 课程名称（关联查询用）
    private LocalDateTime createTime;// 选课时间

    public ChildCourse() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getChildId() { return childId; }
    public void setChildId(Integer childId) { this.childId = childId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return String.format("ChildCourse{child='%s', course='%s'}", childName, courseName);
    }
}
