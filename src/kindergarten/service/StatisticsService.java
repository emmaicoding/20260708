package kindergarten.service;

import kindergarten.dao.ClassDao;
import kindergarten.dao.CourseDao;
import kindergarten.dao.AttendanceDao;
import kindergarten.dao.ChildDao;
import kindergarten.entity.ClassInfo;
import kindergarten.entity.Child;
import kindergarten.entity.Course;
import kindergarten.exception.DataAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 提供班级人数、课程选课、年级分布、出勤率等统计数据
 */
public class StatisticsService {
    private final ClassDao classDao = new ClassDao();
    private final CourseDao courseDao = new CourseDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final ChildDao childDao = new ChildDao();

    /**
     * 获取班级人数统计（含男女比例）
     *
     * @return 统计列表，每项包含：班级名、年级、当前人数、男生数、女生数、容量
     */
    public List<Map<String, Object>> getClassStatistics() {
        try {
            List<ClassInfo> classes = classDao.selectAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (ClassInfo cls : classes) {
                Map<String, Object> row = new HashMap<>();
                row.put("className", cls.getClassName());
                row.put("grade", cls.getGrade());
                row.put("currentCount", cls.getCurrentCount());
                row.put("maxCount", cls.getMaxCount());
                // 统计男女
                int boyCount = 0, girlCount = 0;
                List<Child> children = childDao.selectByClassId(cls.getId());
                for (Child child : children) {
                    if ("M".equals(child.getGender())) boyCount++;
                    else girlCount++;
                }
                row.put("boyCount", boyCount);
                row.put("girlCount", girlCount);
                result.add(row);
            }
            return result;
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取课程选课统计
     *
     * @return 统计列表，每项包含：课程名、已选人数、容量、选课率
     */
    public List<Map<String, Object>> getCourseStatistics() {
        try {
            List<Course> courses = courseDao.selectAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Course c : courses) {
                Map<String, Object> row = new HashMap<>();
                row.put("courseName", c.getCourseName());
                row.put("currentCount", c.getCurrentCount());
                row.put("maxCount", c.getMaxCount());
                double rate = c.getMaxCount() > 0 ?
                    Math.round(c.getCurrentCount() * 1000.0 / c.getMaxCount()) / 10.0 : 0;
                row.put("rate", rate);
                result.add(row);
            }
            return result;
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取年级分布统计
     *
     * @return 统计列表，每项包含：年级、总人数、班级数、平均人数
     */
    public List<Map<String, Object>> getGradeStatistics() {
        try {
            List<ClassInfo> classes = classDao.selectAll();
            // 按年级汇总
            Map<String, int[]> gradeMap = new HashMap<>(); // [总人数, 班级数]
            for (ClassInfo cls : classes) {
                String grade = cls.getGrade();
                gradeMap.putIfAbsent(grade, new int[]{0, 0});
                gradeMap.get(grade)[0] += cls.getCurrentCount();
                gradeMap.get(grade)[1]++;
            }
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, int[]> entry : gradeMap.entrySet()) {
                Map<String, Object> row = new HashMap<>();
                row.put("grade", entry.getKey());
                int total = entry.getValue()[0];
                int classCount = entry.getValue()[1];
                row.put("totalStudents", total);
                row.put("classCount", classCount);
                row.put("avgPerClass", classCount > 0 ? Math.round(total * 10.0 / classCount) / 10.0 : 0);
                result.add(row);
            }
            return result;
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取各班出勤率对比
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 统计列表，每项包含：班级名、总记录、出勤数、出勤率
     */
    public List<Map<String, Object>> getAttendanceRateReport(LocalDate start, LocalDate end) {
        try {
            List<ClassInfo> classes = classDao.selectAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (ClassInfo cls : classes) {
                int[] stats = attendanceDao.countByClassAndDateRange(cls.getId(), start, end);
                Map<String, Object> row = new HashMap<>();
                row.put("className", cls.getClassName());
                row.put("total", stats[0]);
                row.put("present", stats[1]);
                row.put("absent", stats[2]);
                row.put("leave", stats[3]);
                row.put("late", stats[4]);
                double rate = stats[0] > 0 ?
                    Math.round((stats[1] + stats[4]) * 1000.0 / stats[0]) / 10.0 : 0;
                row.put("rate", rate);
                result.add(row);
            }
            return result;
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取总人数
     *
     * @return 在园幼儿总数
     */
    public int getTotalChildren() {
        try {
            return childDao.selectAll().size();
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 获取总班级数
     *
     * @return 班级总数
     */
    public int getTotalClasses() {
        try {
            return classDao.selectAll().size();
        } catch (DataAccessException e) {
            return 0;
        }
    }
}
