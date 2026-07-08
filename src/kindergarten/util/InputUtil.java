package kindergarten.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * 控制台输入工具类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 封装Scanner操作，提供安全的控制台输入方法，
 *              包含输入校验和错误重试机制。
 */
public class InputUtil {

    /** 全局共享Scanner实例，避免多实例冲突 */
    private static final Scanner scanner = new Scanner(System.in);

    /** 日期格式化器 */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 读取整数输入
     *
     * @param prompt 提示信息
     * @return 用户输入的整数
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  ✗ 输入无效，请输入一个整数！");
            }
        }
    }

    /**
     * 读取指定范围内的整数
     *
     * @param prompt 提示信息
     * @param min    最小值（含）
     * @param max    最大值（含）
     * @return 范围内的整数
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("  ✗ 请输入 " + min + "~" + max + " 之间的数字！");
        }
    }

    /**
     * 读取非空字符串
     *
     * @param prompt 提示信息
     * @return 非空字符串
     */
    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  ✗ 输入不能为空！");
        }
    }

    /**
     * 读取字符串（允许为空，返回默认值）
     *
     * @param prompt      提示信息
     * @param defaultValue 默认值
     * @return 用户输入或默认值
     */
    public static String readString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * 读取日期输入（格式：yyyy-MM-dd）
     *
     * @param prompt 提示信息
     * @return LocalDate对象
     */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + "(格式：yyyy-MM-dd) ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  ✗ 日期格式错误，请使用 yyyy-MM-dd 格式！");
            }
        }
    }

    /**
     * 读取性别输入（M/F）
     *
     * @param prompt 提示信息
     * @return "M" 或 "F"
     */
    public static String readGender(String prompt) {
        while (true) {
            System.out.print(prompt + "(M/F) ");
            String input = scanner.nextLine().trim().toUpperCase();
            if ("M".equals(input) || "F".equals(input)) {
                return input;
            }
            System.out.println("  ✗ 请输入 M（男）或 F（女）！");
        }
    }

    /**
     * 读取确认信息（Y/N）
     *
     * @param prompt 提示信息
     * @return 确认返回true，取消返回false
     */
    public static boolean readConfirm(String prompt) {
        System.out.print(prompt + "(Y/N) ");
        String input = scanner.nextLine().trim().toUpperCase();
        return "Y".equals(input) || "YES".equals(input);
    }

    /**
     * 读取电话号码（11位数字）
     *
     * @param prompt 提示信息
     * @return 电话号码字符串
     */
    public static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{11}")) {
                return input;
            }
            System.out.println("  ✗ 请输入11位手机号码！");
        }
    }

    /**
     * 等待用户按回车继续
     */
    public static void waitForEnter() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }

    /**
     * 获取Scanner实例（供特殊场景使用）
     *
     * @return Scanner实例
     */
    public static Scanner getScanner() {
        return scanner;
    }
}
