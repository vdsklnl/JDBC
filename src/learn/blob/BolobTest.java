package learn.blob;

import learn.bean.Customer;
import learn.util.JDBCUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

/**
 * @author vdsklnl
 * @create 2022-05-21 20:56
 * @Description 测试使用PreparedStatement操作Blob类型数据
 */

public class BolobTest {
    /*
     * 向数据表customers中插入Blob类型字段
     * 插入时可能出错，MySQL配置文件my.ini中，传入文件限制为1M，blob文件过大保存失败
     * 解决方案：在my.ini文件中，加入或覆盖配置max_allowed_packet = 16M，并重启数据库服务
     */
    @Test
    public void testInsert() {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();

            String sql = "insert into customers(name,email,birth,photo) values(?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setObject(1,"胡歌");
            ps.setObject(2,"huge@gmail.com");
            ps.setObject(3,"1980-06-04");

            FileInputStream fis = new FileInputStream(new File("src\\img.png"));
            ps.setBlob(4,fis);

            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }

    //查询数据表customers中Blob类型字段
    @Test
    public void testQuery() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth,photo from customers where name = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,"朱茵");

            rs = ps.executeQuery();
            if(rs.next()) {
    //            //方式一
    //            int id = rs.getInt(1);
    //            String name = rs.getString(2);
    //            String email = rs.getString(3);
    //            Date birth = rs.getDate(4);

                //方式二(推荐，可以不用一一对应)
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");

                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

                //将Blob类型字段下载为文件保存在本地
                //bs，fos未关闭也可以运行，认为自动关闭
                Blob photo = rs.getBlob("photo");
                InputStream bs = photo.getBinaryStream();
                FileOutputStream fos = new FileOutputStream("src\\朱茵.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while((len = bs.read(buffer)) != -1){
                    fos.write(buffer,0,len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }
    }

}
