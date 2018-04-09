package org.helper.model;

import java.util.Date;

/**
 * 测试mysql-mybatis-java之间时间相关转换问题
 * mysql date to Java String
 * mysql date to Java date
 * mysql string to Java date
 * Java String to mysql date
 */
public class DateAndStringTest {
    private Long id;

    private String sqlDateToJavaString;
    private Date sqlDateToJavaDate;

    private String sqlTimeStampToJavaString;
    private Date sqlTimeStampToJavaDate;

    private String javaStringToSqlDate;
    private String javaStringToSqlTimeStamp;

    private Date sqlStringToJavaDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJavaStringToSqlDate() {
        return javaStringToSqlDate;
    }

    public void setJavaStringToSqlDate(String javaStringToSqlDate) {
        this.javaStringToSqlDate = javaStringToSqlDate;
    }

    public String getJavaStringToSqlTimeStamp() {
        return javaStringToSqlTimeStamp;
    }

    public void setJavaStringToSqlTimeStamp(String javaStringToSqlTimeStamp) {
        this.javaStringToSqlTimeStamp = javaStringToSqlTimeStamp;
    }

    public Date getSqlDateToJavaDate() {
        return sqlDateToJavaDate;
    }

    public void setSqlDateToJavaDate(Date sqlDateToJavaDate) {
        this.sqlDateToJavaDate = sqlDateToJavaDate;
    }

    public String getSqlDateToJavaString() {
        return sqlDateToJavaString;
    }

    public void setSqlDateToJavaString(String sqlDateToJavaString) {
        this.sqlDateToJavaString = sqlDateToJavaString;
    }

    public Date getSqlStringToJavaDate() {
        return sqlStringToJavaDate;
    }

    public void setSqlStringToJavaDate(Date sqlStringToJavaDate) {
        this.sqlStringToJavaDate = sqlStringToJavaDate;
    }

    public Date getSqlTimeStampToJavaDate() {
        return sqlTimeStampToJavaDate;
    }

    public void setSqlTimeStampToJavaDate(Date sqlTimeStampToJavaDate) {
        this.sqlTimeStampToJavaDate = sqlTimeStampToJavaDate;
    }

    public String getSqlTimeStampToJavaString() {
        return sqlTimeStampToJavaString;
    }

    public void setSqlTimeStampToJavaString(String sqlTimeStampToJavaString) {
        this.sqlTimeStampToJavaString = sqlTimeStampToJavaString;
    }

    @Override
    public String toString() {
        return "DateAndStringTest{" +
                "id=" + id +
                ", sqlDateToJavaString='" + sqlDateToJavaString + '\'' +
                ", sqlDateToJavaDate=" + sqlDateToJavaDate +
                ", sqlTimeStampToJavaString='" + sqlTimeStampToJavaString + '\'' +
                ", sqlTimeStampToJavaDate=" + sqlTimeStampToJavaDate +
                ", javaStringToSqlDate='" + javaStringToSqlDate + '\'' +
                ", javaStringToSqlTimeStamp='" + javaStringToSqlTimeStamp + '\'' +
                ", sqlStringToJavaDate=" + sqlStringToJavaDate +
                '}';
    }
}
