package org.helper.dao;

import org.helper.model.DateAndStringTest;

import java.util.List;

/**
 * 测试mysql-mybatis-java之间时间相关转换问题
 * mysql date to Java String
 * mysql date to Java date
 * mysql string to Java date
 * Java String to mysql date
 */
public interface DateAndStringTestDao {
    DateAndStringTest selectOneById(Long id);

    List<DateAndStringTest> selectAll();

    Integer insertOne(DateAndStringTest dateAndStringTest);

    Integer insertOne2(DateAndStringTest dateAndStringTest);

}
