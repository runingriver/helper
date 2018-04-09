package org.helper.time;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hzz 17-12-19
 */
public class ReverseTime {
    /**
     * 定长时间反转,时间格式为:122334123的字符串进行反转.
     * 反转逻辑:(23:59:59.999 - 12:23:34.123)得出的时间:11:34:29.864
     *
     * @param time 格式为:122334123
     * @return 反转后的字符串
     */
    public static String fixedTimeReverse(String time) {
        if (StringUtils.isBlank(time) || time.length() != 9) {
            throw new IllegalArgumentException("time format illegal,time:" + time);
        }
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2, 4));
        int second = Integer.parseInt(time.substring(4, 6));
        int millisecond = Integer.parseInt(time.substring(6, 9));
        hour = 23 - hour;
        minute = 59 - minute;
        second = 59 - second;
        millisecond = 999 - millisecond;
        return String.format("%02d%02d%02d%03d", hour, minute, second, millisecond);
    }

    /**
     * 不定长时间反转
     * 反转逻辑:(23:59:59.999 - 12:23:34.123)得出的时间:11:34:29.864
     *
     * @param time 格式列表:12; 1223; 122334; 122334123
     * @return 反转后的字符串
     */
    public static String unfixedTimeReverse(String time) {
        if (StringUtils.isBlank(time)) {
            throw new IllegalArgumentException("time format illegal,time:" + time);
        }
        StringBuilder timeBuilder = new StringBuilder(9);
        int size = time.length();
        if (size >= 2) {
            int hour = Integer.parseInt(time.substring(0, 2));
            hour = 23 - hour;
            if (hour < 10) {
                timeBuilder.append('0').append(hour);
            } else {
                timeBuilder.append(hour);
            }
        }

        if (size >= 4) {
            int minute = Integer.parseInt(time.substring(2, 4));
            minute = 59 - minute;
            if (minute < 10) {
                timeBuilder.append('0').append(minute);
            } else {
                timeBuilder.append(minute);
            }
        }

        if (size >= 6) {
            int second = Integer.parseInt(time.substring(4, 6));
            second = 59 - second;
            if (second < 10) {
                timeBuilder.append('0').append(second);
            } else {
                timeBuilder.append(second);
            }
        }

        if (size == 9) {
            int millisecond = Integer.parseInt(time.substring(6, 9));
            millisecond = 999 - millisecond;
            if (millisecond < 10) {
                timeBuilder.append("00").append(millisecond);
            } else if (millisecond < 100) {
                timeBuilder.append('0').append(millisecond);
            } else {
                timeBuilder.append(millisecond);
            }
        }

        return timeBuilder.toString();
    }
}
