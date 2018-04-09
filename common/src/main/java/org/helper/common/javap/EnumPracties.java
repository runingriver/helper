package org.helper.common.javap;

import java.util.HashMap;
import java.util.Map;

/**
 * 分析枚举类的结构,实现原理.
 */
public enum EnumPracties {
    MONDAY("MONDAY", "星期一") {
        @Override
        public String processor(String str1, String str2) {
            return str1 + str2 + getEnglish() + " " + getChinese() + " so tied";
        }
    },
    TUESDAY("TUESHDAY", "星期二") {
        @Override
        public String processor(String str1, String str2) {
            return str1 + str2 + getEnglish() + " " + getChinese() + " Friday too long.";
        }
    },
    WEDNESDAY("WEDNESDAY", "星期三") {
        @Override
        public String processor(String str1, String str2) {
            return str1 + str2 + getEnglish() + " " + getChinese() + " I am  feel ok!";
        }
    };

    private String english;
    private String Chinese;

    EnumPracties(String english, String Chinese) {
        this.english = english;
        this.Chinese = Chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return Chinese;
    }

    public void setChinese(String chinese) {
        Chinese = chinese;
    }

    public abstract String processor(String str1, String str2);

    private static final Map<String, String> enumMap = new HashMap<>();

    static {
        enumMap.put("key1", "value1");
        enumMap.put("key2", "value2");
        enumMap.put("key3", "value3");
    }
}
