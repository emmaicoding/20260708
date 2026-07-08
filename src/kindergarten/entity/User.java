package kindergarten.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 对应t_user表，存储系统用户（管理员/教师）信息
 */
public class User {
    private Integer id;           // 用户ID
    private String username;      // 用户名
    private String password;      // 密码
    private String realName;      // 真实姓名
    private Integer role;         // 角色：1=管理员，2=教师
    private Integer classId;      // 教师负责班级ID（管理员为null）
    private String className;     // 班级名称（关联查询用，非数据库字段）
    private LocalDateTime createTime; // 创建时间

    public User() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    public Integer getClassId() { return classId; }
    public void setClassId(Integer classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /** 获取角色名称 */
    public String getRoleName() {
        return role != null && role == 1 ? "管理员" : "教师";
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', realName='%s', role='%s'}",
            id, username, realName, getRoleName());
    }
}
