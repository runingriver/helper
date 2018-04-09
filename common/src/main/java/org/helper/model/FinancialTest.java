package org.helper.model;


public class FinancialTest {
    private Long id;
    private String mobile;
    private int weekday_msg_count;
    private int weekend_msg_count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getWeekday_msg_count() {
        return weekday_msg_count;
    }

    public void setWeekday_msg_count(int weekday_msg_count) {
        this.weekday_msg_count = weekday_msg_count;
    }

    public int getWeekend_msg_count() {
        return weekend_msg_count;
    }

    public void setWeekend_msg_count(int weekend_msg_count) {
        this.weekend_msg_count = weekend_msg_count;
    }

    @Override public String toString() {
        return "FinancialTest{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", weekday_msg_count=" + weekday_msg_count +
                ", weekend_msg_count=" + weekend_msg_count +
                '}';
    }
}
