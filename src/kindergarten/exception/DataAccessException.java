package kindergarten.exception;

/**
 * 数据访问异常
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装DAO层SQLException的运行时异常，由Service层捕获处理
 */
public class DataAccessException extends RuntimeException {

    /**
     * 构造数据访问异常
     *
     * @param message 错误描述
     * @param cause   原始异常
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造数据访问异常
     *
     * @param message 错误描述
     */
    public DataAccessException(String message) {
        super(message);
    }
}
