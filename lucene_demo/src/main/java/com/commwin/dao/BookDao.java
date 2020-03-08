package com.commwin.dao;

import com.commwin.domain.Book;

import java.util.List;

public interface BookDao {

    /**
     * findAll
     */
    List<Book> findAllBooks();

}
