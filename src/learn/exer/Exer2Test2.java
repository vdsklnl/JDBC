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
 * @create 2022-05-21 16:54
 * @Description
 */

public class Exer2Test2 {

    //2.根据身份证号或者准考证号查询学生成绩
    public static void main(String[] args) {
        System.out.println("请选择您要输入的类型：");
        System.out.println("a.身份证号/b.准考证号");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.next();
        if("a".equalsIgnoreCase(option)) {

            System.out.println("请输入身份证号：");
            String IDCard = scanner.next();
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name," +
                    "Location location,Grade grade from examstudent where IDCard = ?";
            Student student = getInstance(Student.class, sql, IDCard);
            if(student != null) {
                System.out.println(student);
            } else {
                System.out.println("输入身份证号有误！");
            }

        } else if("b".equalsIgnoreCase(option)) {

            System.out.println("请输入准考证号：");
            String examCard = scanner.next();
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name," +
                    "Location location,Grade grade from examstudent where ExamCard = ?";
            Student student = getInstance(Student.class, sql, examCard);
            if(student != null) {
                System.out.println(student);
            } else {
                System.out.println("输入准考证号有误！");
            }

        } else {
            System.out.println("您的输入有误，请重新进入程序！");
        }
    }

    public static <T> T getInstance(Class<T> clazz, String sql, Object ... args) {
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
