package learn.preparedStatement.crud;

import learn.bean.Customer;
import learn.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @author vdsklnl
 * @create 2022-05-20 16:59
 * @Description 针对Customers表的通用查询操作
 */

public class CustomersForQuery {

    //测试queryForCustomers
    @Test
    public void testQueryForCustomers() {

        String sql = "select id,name,birth,email from customers where id = ?";
        Customer cust = queryForCustomers(sql, 13);
        System.out.println(cust);

        String sql1 = "select name,email from customers where name = ?";
        Customer cust1 = queryForCustomers(sql1, "周杰伦");
        System.out.println(cust1);

    }

    //针对customers表操作
    public Customer queryForCustomers(String sql, Object ... args) {

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

            //获取结果集元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();

            //通过ResultSetMetaData获取结果集中列数
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {
                //当有结果时，先造出对象，再通过set方法设置
                Customer cust = new Customer();

                for (int i = 0; i < columnCount; i++) {

                    Object value = rs.getObject(i + 1);

                    //给Customer某条属性赋值为value，需取列名
                    String columnName = rsmd.getColumnLabel(i + 1);

                    //通过反射获取
                    Field field = Customer.class.getDeclaredField(columnName);
                    //设置私有属性可访问
                    field.setAccessible(true);
                    field.set(cust, value);

                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }

    //测试，查询一条数据
    @Test
    public void testQuery1() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,1);

            //返回结果集
            resultSet = ps.executeQuery();

            //处理，使用literator迭代器
            //next:判断结果集下一条是否有数据，有返回true，指针下移；无返回false,结束
            if(resultSet.next()){
                //获取当前数据各field值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //处理方式可以输出或存放集合，但应该生成对应对象封装
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源(包含resultSet，重载方法)
            JDBCUtils.closeResource(conn,ps,resultSet);
        }
    }

}
