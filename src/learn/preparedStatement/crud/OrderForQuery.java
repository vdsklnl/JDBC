package learn.preparedStatement.crud;

import learn.bean.Order;
import learn.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author vdsklnl
 * @create 2022-05-20 20:47
 * @Description 针对Order表查询操作
 */

public class OrderForQuery {

    /*
     * 针对表的字段名和类属性名不同的情况：
     * 1. 声明sql时，使用类的属性名命名字段别名
     * 2. 使用ResultSetMetaData时，用getColumnLabel()替换getColumnName()获取别名
     * 3. 当无别名时，getColumnLabel()获取列名，推荐使用
     */

    @Test
    public void testQueryForOrder() {
        //使用别名使结果集中字段名和类名相同
        String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = queryForOrder(sql, 1);
        System.out.println(order);
    }

    public Order queryForOrder(String sql, Object ... args) {
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

            if (rs.next()) {
                Order order = new Order();
                for (int i = 0; i < columnCount; i++) {
                    Object value = rs.getObject(i + 1);

                    //获取列的列名：getColumnName()
                    //获取列的别名：getColumnLabel() 推荐使用，当无别名则就是列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnName = rsmd.getColumnLabel(i + 1);

                    Field field = Order.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(order, value);
                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
        return null;
    }

}
