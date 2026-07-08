package kindergarten.service;

import kindergarten.dao.ClassDao;
import kindergarten.entity.ClassInfo;

/**
 * 班级业务逻辑层
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装班级相关的业务查询操作
 */
public class ClassService {
    private final ClassDao classDao = new ClassDao();

    /**
     * 根据ID查询班级
     *
     * @param id 班级ID
     * @return 班级对象，不存在返回null
     */
    public ClassInfo getClassById(int id) {
        return classDao.selectById(id);
    }

    /**
     * 查询所有班级
     *
     * @return 班级列表
     */
    public java.util.List<ClassInfo> getAllClasses() {
        return classDao.selectAll();
    }
}
