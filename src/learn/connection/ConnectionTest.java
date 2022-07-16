package learn.connection;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author vdsklnl
 * @create 2022-05-19 16:53
 * @Description
 */
public class ConnectionTest {

    //方式一：
    @Test
    public void testConnection1() throws SQLException {
        //MySQL驱动加载，资料中
        //获取Driver实现类对象
        Driver driver = new com.mysql.jdbc.Driver();

        //例子url:http://localhost:8080/gmail/key.jpg
        //jdbc:mysql -> 协议
        //localhost -> 本机ip地址
        //3306 -> MySQL数据库默认端口号
        //test -> 连接数据库名称
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";

        //将用户名和密码封装在Properties中，存放键值对
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","lgbtqiapkdx");

        Connection connect = driver.connect(url, info);
        System.out.println(connect);

    }

    //方式二(减少第三方包出现，提高代码适用性)：
    @Test
    public void testConnection2() throws Exception {

        //使用反射获取相应对象
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();

        //例子url:http://localhost:8080/gmail/key.jpg
        //jdbc:mysql -> 协议
        //localhost -> 本机ip地址
        //3306 -> MySQL数据库默认端口号
        //test -> 连接数据库名称
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";

        //将用户名和密码封装在Properties中，存放键值对
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","lgbtqiapkdx");

        Connection connect = driver.connect(url, info);
        System.out.println(connect);

    }

    //方式三(使用DriverManager替换Driver):
    @Test
    public void testConnection3() throws Exception {
        //相应信息
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
        String user = "root";
        String password = "lgbtqiapkdx";

        //使用反射获取相应对象
        Class clazz = Class.forName("com.mysql.jdbc.Driver");
        //加载驱动
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();

        //DriverManager
        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式四(仅加载驱动，MySQL Driver类自动注册)：
    @Test
    public void testConnection4() throws Exception {
        //相应信息
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
        String user = "root";
        String password = "lgbtqiapkdx";

        //MySQL 中可以省去，在jar包配置文件中给出(META—INF)，但其它数据库不一定，建议保留
        //使用反射获取相应对象
        Class.forName("com.mysql.jdbc.Driver");
//        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
//        //DriverManager
//        //注册驱动
//        DriverManager.registerDriver(driver);
        /*
        MySQL Driver类静态代码块
        static {
            try {
                DriverManager.registerDriver(new Driver());
            } catch (SQLException var1) {
                throw new RuntimeException("Can't register driver!");
            }
        }
         */

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);

    }

    //方式五(将基础信息写入配置文件，直接读取)：
    /*
    1.解耦，数据与代码分离
    2.需要则修改配置文件信息，避免程序重新打包
     */
    @Test
    public void testConnection5() throws Exception {

        //读取配置文件流
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        //配置文件对象
        Properties pros = new Properties();
        pros.load(is);

        //读取配置文件内容并赋值
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //加载驱动
        Class.forName(driverClass);

        //获取连接
        Connection connection = DriverManager.getConnection(url,user, password);
        System.out.println(connection);

    }
}
