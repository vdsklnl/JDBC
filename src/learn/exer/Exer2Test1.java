package learn.exer;

import learn.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @author vdsklnl
 * @create 2022-05-21 16:02
 * @Description
 */

public class Exer2Test1 {

    //1.向examstudent表中添加一条记录
    public static void main(String[] args) {
        /*
        Type/IDCard/ExamCard/StudentName/Location/Grade
         */
        Scanner scanner = new Scanner(System.in);
        System.out.print("四级/六级：");
        int type = scanner.nextInt();
        System.out.print("身份证号：");
        String IDCard = scanner.next();
        System.out.print("准考证号：");
        String examCard = scanner.next();
        System.out.print("学生姓名：");
        String studentName = scanner.next();
        System.out.print("地址：");
        String location = scanner.next();
        System.out.print("成绩：");
        int grade = scanner.nextInt();

        String sql = "insert into examstudent(type,IDCard,examCard,studentName,location,grade) values(?,?,?,?,?,?)";
        int update = update(sql, type, IDCard, examCard, studentName, location, grade);
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
