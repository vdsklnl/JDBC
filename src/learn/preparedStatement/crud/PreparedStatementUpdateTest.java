package learn.preparedStatement.crud;

import learn.util.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @author vdsklnl
 * @create 2022-05-20 12:57
 * @Description 使用PreparedStatement替代Statement
 *              实现对数据表的增删改查操作。
 */

public class PreparedStatementUpdateTest {

    @Test
    public void testCommenUpdate() {

//        String sql = "delete from customers where id = ?";
//        update(sql,19);

        //注意着重号区分关键字
        String sql = "update `order` set order_name = ? where order_id = ?";
        update(sql,"DD",2);

    }

    //通用增删改操作(连接数据库中任意表)
    //sql占位符个数与可变形参个数相同
    public void update(String sql,Object ... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //获取连接
            conn = JDBCUtils.getConnection();
            //生成PreparedStatement实例
            ps = conn.prepareStatement(sql);
            //填充占位符(数据库从1开始，数组从0开始)
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1,args[i]);
            }
            //执行操作
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtils.closeResource(conn,ps);
        }

    }

    //修改customers表的一条记录
    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //获取连接
            conn = JDBCUtils.getConnection();

            //预编译sql语句，返回PreparedStatement实例
            String sql = "UPDATE customers SET name = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);

            //填充占位符
            ps.setObject(1,"莫扎特");
            ps.setObject(2,18);

            //执行操作
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtils.closeResource(conn,ps);
        }

    }

    //向customer表中添加一条记录
    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
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
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(conn);

            //预编译sql语句，返回PreparedStatement实例
            //?为占位符
            String sql = "INSERT INTO customers(name,email,birth) VALUES(?,?,?)";
            ps = conn.prepareStatement(sql);

            //填充占位符
            ps.setString(1,"哪吒");
            ps.setString(2,"neza.@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("1000-01-01");
            ps.setDate(3, new Date(date.getTime()));

            //执行操作
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            //资源关闭
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

    }

}
