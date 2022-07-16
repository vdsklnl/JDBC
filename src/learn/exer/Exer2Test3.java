package learn.exer;

import learn.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @author vdsklnl
 * @create 2022-05-21 19:12
 * @Description
 */

public class Exer2Test3 {

    //查询学生考号并删除
    public static void main(String[] args) {

//        System.out.println("请输入学生准考证号：");
//        Scanner scanner = new Scanner(System.in);
//        String examCard = scanner.next();
//
//        //查询指定准考证号学生
//        String sqlQuery = "select FlowID flowID,Type type,IDCard,ExamCard examCard,StudentName name," +
//                "Location location,Grade grade from examstudent where ExamCard = ?";
//        Student student = getInstance(Student.class, sqlQuery, examCard);
//        if(student == null) {
//            System.out.println("查无此人，请重新输入！");
//        } else {
//            String sqlUpdate = "delete from examstudent where ExamCard = ?";
//            int update = update(sqlUpdate, examCard);
//            if(update > 0) {
//                System.out.println("操作成功！");
//            } else {
//                System.out.println("操作失败！");
//            }
//        }

        //也可以不进行查询，直接删除，MySQL不会报错
        System.out.println("请输入学生准考证号：");
        Scanner scanner = new Scanner(System.in);
        String examCard = scanner.next();
        String sqlUpdate = "delete from examstudent where ExamCard = ?";
        int update = update(sqlUpdate, examCard);
        if(update > 0) {
            System.out.println("操作成功！");
        } else {
            System.out.println("查无此人，请重新输入！");
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
