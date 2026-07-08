package kindergarten;

import kindergarten.view.MainView;

/**
 * 幼儿园管理系统主入口
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 程序入口类，启动幼儿园管理系统控制台应用。
 *              系统启动后自动初始化数据库，用户通过控制台菜单进行操作。
 */
public class Main {

    /**
     * 程序入口
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("     幼儿园管理系统 v1.0");
        System.out.println("     开发团队：[请填写团队名称]");
        System.out.println("     开发日期：2026-07-06");
        System.out.println("================================================");

        try {
            // 加载MySQL JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("\n✗ 未找到MySQL JDBC驱动！");
            System.out.println("  请将 mysql-connector-j-8.0.xx.jar 放入 lib 目录");
            System.out.println("  并确保运行时classpath中包含该jar包");
            return;
        }

        // 启动系统主界面
        new MainView().start();
    }
}