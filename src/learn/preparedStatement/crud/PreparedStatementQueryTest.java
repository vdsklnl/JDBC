package learn.preparedStatement.crud;

import learn.bean.Customer;
import learn.bean.Order;
import learn.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vdsklnl
 * @create 2022-05-21 11:39
 * @Description 使用PreparedStatement实现对于不同表的通用查询操作
 */

/* PreparedStatement优势
 * 1.PreparedStatement对sql语句预编译，保证逻辑不被更改
 * 2.可以操作Blob数据，占位符用流替代
 * 3.可以实现高效批量操作过程，减少SQL语句编写
 */

public class PreparedStatementQueryTest {

    //占位符仅限于过滤条件
    @Test
    public void testGetForList() {

        String sql = "select id,name,email,birth from customers where id < ?";
        List<Customer> list = getForList(Customer.class, sql, 12);
        list.forEach(System.out::println);

        String sql1 = "select order_id orderId,order_name orderName from `order` where order_id < ?";
        List<Order> list1 = getForList(Order.class, sql1, 5);
        list1.forEach(System.out::println);

    }

    //体现多态性，返回接口具体实现类对象，调用相应实现类重写方法
    public <T> List<T> getForList(Class<T> clazz, String sql, Object ... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            //创建集合
            ArrayList<T> list = new ArrayList<>();

            //多条数据，改用while
            while(rs.next()) {
                //使用泛型生成对象,使用clazz
                T t = clazz.getDeclaredConstructor().newInstance();
                //给t对象属性赋值
                for (int i = 0; i < columnCount; i++) {
                    Object value = rs.getObject(i + 1);
                    String label = rsmd.getColumnLabel(i + 1);

                    //使用数据表对应类
                    Field field = clazz.getDeclaredField(label);
                    field.setAccessible(true);
                    field.set(t,value);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }

    @Test
    public void testGetInstance() {

        String sql = "select id,name,email from customers where id = ?";
        Customer customer = getInstance(Customer.class, sql, 12);
        System.out.println(customer);

        String sql1 = "select order_id orderId,order_name orderName from `order` where order_id = ?";
        Order order = getInstance(Order.class, sql1, 1);
        System.out.println(order);

    }

    public <T> T getInstance(Class<T> clazz, String sql, Object ... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if(rs.next()) {
                //使用泛型生成对象,使用clazz
                T t = clazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object value = rs.getObject(i + 1);
                    String label = rsmd.getColumnLabel(i + 1);

                    //使用数据表对应类
                    Field field = clazz.getDeclaredField(label);
                    field.setAccessible(true);
                    field.set(t,value);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }
}
