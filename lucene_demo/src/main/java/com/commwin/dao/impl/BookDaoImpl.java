package com.commwin.dao.impl;

import com.commwin.dao.BookDao;
import com.commwin.domain.Book;
import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

//    1.使用jdbc进行处理：
    @Override
    public List<Book> findAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;

        try {
//            加载驱动：使用javaSPI机制
           Class.forName("com.mysql.cj.jdbc.Driver");
//           2.建立连接：
          connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lucene_demo?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT","root","root");
//          3.创建sql语句：connection
            preparedStatement  = connection.prepareStatement("select * from book");
//          4.执行查询：
             rs = preparedStatement.executeQuery();

            Book book = null;
            //遍历查询结果
            while(rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookname(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookdesc(rs.getString("bookdesc"));
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDaoImpl();
        System.out.println(bookDao.findAllBooks());
    }
}
