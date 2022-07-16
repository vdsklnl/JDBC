package learn.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author vdsklnl
 * @create 2022-05-20 15:58
 * @Description 封装获取连接和基本操作
 */

public class JDBCUtils {

    //获取数据库连接
    public static Connection getConnection() throws Exception {

        //读取配置文件,ClassLoader获取系统类加载器
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //加载驱动
        Class.forName(driverClass);

        //获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    //关闭资源
    public static void closeResource(Connection conn, Statement ps) {
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
