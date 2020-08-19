package haven.fenghua.exception;

/**
 * 通用异常类
 *
 * @author zhangcg
 * @version 1.0
 * @date 2020年05月26日 下午16:12:37
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(Throwable cause) {
        super(cause);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }
}
