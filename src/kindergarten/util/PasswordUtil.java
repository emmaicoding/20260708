package kindergarten.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码哈希工具类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 提供基于SHA-256的密码哈希和验证功能
 */
public class PasswordUtil {

    /** 哈希算法 */
    private static final String ALGORITHM = "SHA-256";

    /**
     * 对明文密码进行SHA-256哈希
     *
     * @param password 明文密码
     * @return 哈希后的十六进制字符串
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("密码不能为null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * 验证明文密码与哈希是否匹配
     *
     * @param password 明文密码
     * @param hashed   存储的哈希值
     * @return 匹配返回true
     */
    public static boolean verify(String password, String hashed) {
        if (password == null || hashed == null) {
            return false;
        }
        return hash(password).equals(hashed);
    }
}
