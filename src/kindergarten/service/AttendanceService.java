package kindergarten.service;

import kindergarten.dao.AttendanceDao;
import kindergarten.dao.ChildDao;
import kindergarten.entity.Attendance;
import kindergarten.entity.Child;
import kindergarten.exception.DataAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 考勤业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 处理每日考勤打卡、历史查询和出勤率统计
 */
public class AttendanceService {
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final ChildDao childDao = new ChildDao();

    /**
     * 记录单个幼儿的考勤
     *
     * @param childId 幼儿ID
     * @param date    考勤日期
     * @param status  状态：1出勤 2缺勤 3请假 4迟到
     * @param remark  备注（可为null）
     * @return 结果描述
     */
    public String recordAttendance(int childId, LocalDate date, int status, String remark) {
        if (status < 1 || status > 4) {
            return "考勤状态无效（1出勤/2缺勤/3请假/4迟到）";
        }
        try {
            Child child = childDao.selectById(childId);
            if (child == null || child.getStatus() != 1) {
                return "幼儿不存在或已离园";
            }
            Attendance att = new Attendance();
            att.setChildId(childId);
            att.setAttendDate(date);
            att.setStatus(status);
            att.setRemark(remark);
            int rows = attendanceDao.insertOrUpdate(att);
            return rows > 0 ? "考勤已记录：" + child.getName() + " - " + getStatusName(status) : "记录失败";
        } catch (DataAccessException e) {
            return "操作失败：" + e.getMessage();
        }
    }

    /**
     * 为整个班级批量记录考勤（全部设为同一状态）
     *
     * @param classId 班级ID
     * @param date    考勤日期
     * @param status  状态
     * @return 成功记录人数
     */
    public int batchRecordByClass(int classId, LocalDate date, int status) {
        try {
            List<Child> children = childDao.selectByClassId(classId);
            int count = 0;
            for (Child child : children) {
                Attendance att = new Attendance();
                att.setChildId(child.getId());
                att.setAttendDate(date);
                att.setStatus(status);
                att.setRemark(null);
                if (attendanceDao.insertOrUpdate(att) > 0) count++;
            }
            return count;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 查询某班某日考勤
     *
     * @param classId 班级ID
     * @param date    日期
     * @return 考勤列表
     */
    public List<Attendance> getClassAttendance(int classId, LocalDate date) {
        try {
            return attendanceDao.selectByClassAndDate(classId, date);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 查询幼儿历史考勤
     *
     * @param childId 幼儿ID
     * @param start   开始日期
     * @param end     结束日期
     * @return 考勤列表
     */
    public List<Attendance> getChildAttendance(int childId, LocalDate start, LocalDate end) {
        try {
            return attendanceDao.selectByChildAndDateRange(childId, start, end);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 统计班级出勤率
     *
     * @param classId 班级ID
     * @param start   开始日期
     * @param end     结束日期
     * @return 统计数组：[总记录数, 出勤数, 缺勤数, 请假数, 迟到数]
     */
    public int[] getClassAttendanceStats(int classId, LocalDate start, LocalDate end) {
        try {
            return attendanceDao.countByClassAndDateRange(classId, start, end);
        } catch (DataAccessException e) {
            return new int[]{0, 0, 0, 0, 0};
        }
    }

    /**
     * 计算出勤率
     *
     * @param stats 统计数组
     * @return 出勤率百分比（保留1位小数）
     */
    public double calcAttendanceRate(int[] stats) {
        if (stats[0] == 0) return 0.0;
        // 出勤率 = (出勤 + 迟到) / 总数 * 100
        return Math.round((stats[1] + stats[4]) * 1000.0 / stats[0]) / 10.0;
    }

    /**
     * 获取考勤状态中文名
     *
     * @param status 状态码
     * @return 中文名
     */
    public String getStatusName(int status) {
        switch (status) {
            case 1: return "出勤";
            case 2: return "缺勤";
            case 3: return "请假";
            case 4: return "迟到";
            default: return "未知";
        }
    }
}
