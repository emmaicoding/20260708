package kindergarten.service;

import kindergarten.dao.ChildDao;
import kindergarten.dao.ChildCourseDao;
import kindergarten.dao.AttendanceDao;
import kindergarten.entity.Child;
import kindergarten.entity.ClassInfo;
import kindergarten.exception.DataAccessException;

import java.util.List;

/**
 * 幼儿学籍业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理幼儿信息的增删改查业务，包含数据校验和级联操作
 */
public class ChildService {
    private final ChildDao childDao = new ChildDao();
    private final ChildCourseDao childCourseDao = new ChildCourseDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();

    /**
     * 添加幼儿
     *
     * @param child 幼儿实体
     * @return 结果描述
     */
    public String addChild(Child child) {
        // 校验必填字段
        if (child.getName() == null || child.getName().trim().isEmpty()) {
            return "幼儿姓名不能为空";
        }
        if (child.getClassId() == null || child.getClassId() <= 0) {
            return "请选择所属班级";
        }
        if (child.getBirthDate() == null) {
            return "请填写出生日期";
        }
        if (child.getParentName() == null || child.getParentName().trim().isEmpty()) {
            return "请填写家长姓名";
        }
        if (child.getParentPhone() == null || child.getParentPhone().trim().isEmpty()) {
            return "请填写家长电话";
        }
        try {
            // 校验班级人数
            ClassService classService = new ClassService();
            ClassInfo classInfo = classService.getClassById(child.getClassId());
            if (classInfo == null) {
                return "班级不存在";
            }
            int currentCount = childDao.countByClassId(child.getClassId());
            if (currentCount >= classInfo.getMaxCount()) {
                return "该班级人数已满（" + classInfo.getMaxCount() + "人）";
            }
            child.setName(child.getName().trim());
            child.setParentName(child.getParentName().trim());
            int id = childDao.insert(child);
            return id > 0 ? "添加成功（ID：" + id + "）" : "添加失败";
        } catch (DataAccessException e) {
            return "添加失败：" + e.getMessage();
        }
    }

    /**
     * 修改幼儿信息
     *
     * @param child 幼儿实体
     * @return 结果描述
     */
    public String updateChild(Child child) {
        if (child.getId() == null || child.getId() <= 0) {
            return "幼儿ID无效";
        }
        try {
            Child existing = childDao.selectById(child.getId());
            if (existing == null) {
                return "幼儿不存在";
            }
            if (child.getName() == null || child.getName().trim().isEmpty()) {
                return "幼儿姓名不能为空";
            }
            int rows = childDao.update(child);
            return rows > 0 ? "修改成功" : "修改失败";
        } catch (DataAccessException e) {
            return "修改失败：" + e.getMessage();
        }
    }

    /**
     * 删除幼儿（软删除 + 级联清理选课和考勤）
     *
     * @param childId 幼儿ID
     * @return 结果描述
     */
    public String deleteChild(int childId) {
        try {
            Child existing = childDao.selectById(childId);
            if (existing == null) {
                return "幼儿不存在";
            }
            if (existing.getStatus() != null && existing.getStatus() == 0) {
                return "该幼儿已离园";
            }
            // 级联清理选课记录
            childCourseDao.deleteByChildId(childId);
            // 级联清理考勤记录
            attendanceDao.deleteByChildId(childId);
            // 软删除幼儿
            int rows = childDao.delete(childId);
            return rows > 0 ? "删除成功（" + existing.getName() + "已标记离园）" : "删除失败";
        } catch (DataAccessException e) {
            return "删除失败：" + e.getMessage();
        }
    }

    /**
     * 根据ID查询幼儿详情
     *
     * @param id 幼儿ID
     * @return 幼儿对象
     */
    public Child getChildById(int id) {
        try {
            return childDao.selectById(id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 根据姓名搜索幼儿
     *
     * @param name 姓名关键字
     * @return 幼儿列表
     */
    public List<Child> searchByName(String name) {
        try {
            return childDao.selectByName(name);
        } catch (DataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 查询指定班级的幼儿列表
     *
     * @param classId 班级ID
     * @return 幼儿列表
     */
    public List<Child> getChildrenByClass(int classId) {
        try {
            return childDao.selectByClassId(classId);
        } catch (DataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 查询所有在园幼儿
     *
     * @return 幼儿列表
     */
    public List<Child> getAllChildren() {
        try {
            return childDao.selectAll();
        } catch (DataAccessException e) {
            return new java.util.ArrayList<>();
        }
    }
}
