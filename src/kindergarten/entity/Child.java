package kindergarten.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 幼儿实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_child表，存储幼儿基本信息
 */
public class Child {
    private Integer id;              // 幼儿ID
    private String name;             // 姓名
    private String gender;           // 性别：M=男，F=女
    private LocalDate birthDate;     // 出生日期
    private String parentName;       // 家长姓名
    private String parentPhone;      // 家长电话
    private Integer classId;         // 所在班级ID
    private String className;        // 班级名称（关联查询用）
    private LocalDate enrollmentDate;// 入园日期
    private Integer status;          // 状态：1=在园，0=离园
    private LocalDateTime createTime;// 创建时间

    public Child() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /** 获取性别中文名 */
    public String getGenderName() {
        return "M".equals(gender) ? "男" : "女";
    }

    /** 获取状态中文名 */
    public String getStatusName() {
        return status != null && status == 1 ? "在园" : "离园";
    }

    @Override
    public String toString() {
        return String.format("Child{id=%d, name='%s', gender='%s', class='%s', status='%s'}",
            id, name, getGenderName(), className, getStatusName());
    }
}
