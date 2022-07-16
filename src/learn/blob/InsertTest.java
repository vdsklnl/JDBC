package learn.blob;

import learn.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author vdsklnl
 * @create 2022-05-22 15:08
 * @Description 使用PreparedStatement实现数据的批量操作
 *              Update、Delete本身具有批量操作数据的效果
 *              完成更高效率的批量Insert数据
 */

public class InsertTest {
    //方式一：Statement

    //方式二：PreparedStatement
    @Test
    public void testInsert1() {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 20000 ; i++) {
                //每读取一次与数据库交互一次，效率低
                ps.setObject(1,"name" + i);
                ps.execute();
            }

            long end = System.currentTimeMillis();
            System.out.println("时间：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }

    }

    //方式三(类似读取文件缓冲区，先批量读取，再批量插入，减少交互次数)：
    /* addBatch(),executeBatch(),clearBatch()
     * MySQL服务器默认不支持批量数据处理，需要设置参数，开放支持
     * ?rewriteBatchedStatements=true
     * 放在配置文件url后面(前已有?则改为&)
     * 5.1.7jar包不支持批量操作，需改为5.1.37jar包
     */
    @Test
    public void testInsert2() {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 20000 ; i++) {
                ps.setObject(1,"name" + i);

                //1.积累sql语句
                ps.addBatch();

                //缓冲区大小也影响效率
                if(i % 500 == 0) {
                    //2.执行batch
                    ps.executeBatch();
                    //3.清空batch
                    ps.clearBatch();
                }

            }

            long end = System.currentTimeMillis();
            System.out.println("时间：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }

    }

    //方式四(设置不允许自动提交数据)：
    @Test
    public void testInsert3() {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            //设置不允许自动提交数据，传完一起提交
            //set autocommit = false, MySQL 中
            conn.setAutoCommit(false);

            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);

            for (int i = 1; i <= 20000 ; i++) {
                ps.setObject(1,"name" + i);

                //1.积累sql语句
                ps.addBatch();

                if(i % 500 == 0) {
                    //2.执行batch
                    ps.executeBatch();
                    //3.清空batch
                    ps.clearBatch();
                }

            }

            conn.commit();
            long end = System.currentTimeMillis();
            System.out.println("时间：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }

    }

}
