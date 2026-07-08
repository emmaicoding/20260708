package kindergarten.service;

import kindergarten.dao.CourseDao;
import kindergarten.dao.ChildCourseDao;
import kindergarten.dao.ChildDao;
import kindergarten.entity.Course;
import kindergarten.entity.Child;
import kindergarten.entity.ChildCourse;
import kindergarten.exception.DataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理课程的增删改查、选课、退课等业务，
 *              包含课程容量校验和每人选课数量限制
 */
public class CourseService {
    private final CourseDao courseDao = new CourseDao();
    private final ChildCourseDao childCourseDao = new ChildCourseDao();
    private final ChildDao childDao = new ChildDao();

    /** 每个幼儿最多选课数 */
    private static final int MAX_COURSES_PER_CHILD = 4;

    /**
     * 添加课程
     *
     * @param course 课程实体
     * @return 结果描述
     */
    public String addCourse(Course course) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return "课程名称不能为空";
        }
        if (course.getMaxCount() == null || course.getMaxCount() <= 0) {
            return "课程容量必须大于0";
        }
        try {
            course.setCourseName(course.getCourseName().trim());
            int id = courseDao.insert(course);
            return id > 0 ? "课程添加成功（ID：" + id + "）" : "课程添加失败，可能名称已存在";
        } catch (DataAccessException e) {
            return "课程添加失败：" + e.getMessage();
        }
    }

    /**
     * 修改课程信息
     *
     * @param course 课程实体
     * @return 结果描述
     */
    public String updateCourse(Course course) {
        if (course.getId() == null) return "课程ID无效";
        try {
            Course existing = courseDao.selectById(course.getId());
            if (existing == null) return "课程不存在";
            if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
                return "课程名称不能为空";
            }
            if (course.getMaxCount() < existing.getCurrentCount()) {
                return "容量不能小于当前选课人数（" + existing.getCurrentCount() + "人）";
            }
            int rows = courseDao.update(course);
            return rows > 0 ? "修改成功" : "修改失败";
        } catch (DataAccessException e) {
            return "修改失败：" + e.getMessage();
        }
    }

    /**
     * 删除课程
     *
     * @param courseId 课程ID
     * @return 结果描述
     */
    public String deleteCourse(int courseId) {
        try {
            Course existing = courseDao.selectById(courseId);
            if (existing == null) return "课程不存在";
            if (existing.getCurrentCount() > 0) {
                return "该课程还有" + existing.getCurrentCount() + "名学员，请先清退学员";
            }
            int rows = courseDao.delete(courseId);
            return rows > 0 ? "课程已删除" : "删除失败";
        } catch (DataAccessException e) {
            return "删除失败：" + e.getMessage();
        }
    }

    /**
     * 查询所有课程
     *
     * @return 课程列表
     */
    public List<Course> getAllCourses() {
        try {
            return courseDao.selectAll();
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID查询课程
     *
     * @param id 课程ID
     * @return 课程对象
     */
    public Course getCourseById(int id) {
        try {
            return courseDao.selectById(id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 为幼儿选课
     * 业务规则：
     *   1. 每人最多选4门课
     *   2. 不可重复选同一门课
     *   3. 课程人数未达上限
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 结果描述
     */
    public String selectCourse(int childId, int courseId) {
        try {
            Child child = childDao.selectById(childId);
            if (child == null || child.getStatus() != 1) {
                return "幼儿不存在或已离园";
            }
            Course course = courseDao.selectById(courseId);
            if (course == null) {
                return "课程不存在";
            }
            int count = childCourseDao.countByChildId(childId);
            if (count >= MAX_COURSES_PER_CHILD) {
                return "选课失败：该幼儿已选满" + MAX_COURSES_PER_CHILD + "门课程";
            }
            if (childCourseDao.exists(childId, courseId)) {
                return "选课失败：该幼儿已选过「" + course.getCourseName() + "」";
            }
            if (course.getCurrentCount() >= course.getMaxCount()) {
                return "选课失败：「" + course.getCourseName() + "」已达人数上限（" + course.getMaxCount() + "人）";
            }
            int rows = childCourseDao.insert(childId, courseId);
            return rows > 0 ? "选课成功：" + child.getName() + " → " + course.getCourseName() : "选课失败";
        } catch (DataAccessException e) {
            return "选课失败：" + e.getMessage();
        }
    }

    /**
     * 为幼儿退课
     *
     * @param childId  幼儿ID
     * @param courseId 课程ID
     * @return 结果描述
     */
    public String dropCourse(int childId, int courseId) {
        try {
            if (!childCourseDao.exists(childId, courseId)) {
                return "该幼儿未选此课程";
            }
            int rows = childCourseDao.delete(childId, courseId);
            return rows > 0 ? "退课成功" : "退课失败";
        } catch (DataAccessException e) {
            return "退课失败：" + e.getMessage();
        }
    }

    /**
     * 查询幼儿所学课程
     *
     * @param childId 幼儿ID
     * @return 选课列表
     */
    public List<ChildCourse> getChildCourses(int childId) {
        try {
            return childCourseDao.selectByChildId(childId);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 查询某课程的所有学员
     *
     * @param courseId 课程ID
     * @return 选课列表
     */
    public List<ChildCourse> getCourseStudents(int courseId) {
        try {
            return childCourseDao.selectByCourseId(courseId);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }
}
