package learn.exer;

import learn.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/**
 * @author vdsklnl
 * @create 2022-05-21 15:30
 * @Description
 */

public class Exer1Test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String name = scanner.next();
        System.out.println("请输入邮箱：");
        String email = scanner.next();
        System.out.println("请输入生日：");
        String birth = scanner.next();

        //存在隐式转换"1999-02-02"->date
        String sql = "insert into customers(name,email,birth) values(?,?,?)";
        int update = update(sql, name, email, birth);
        if(update > 0) {
            System.out.println("操作成功！");
        } else {
            System.out.println("操作失败！");
        }
    }

    //通用增删改操作(连接数据库中任意表)
    //sql占位符个数与可变形参个数相同
    public static int update(String sql,Object ... args) {
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
            //ps.excute()返回true/false表示是否返回结果集
//            ps.execute();
            //使用ps.excuteUpdate()返回被影响行数，失败则返回0
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtils.closeResource(conn,ps);
        }
        return 0;
    }
}
