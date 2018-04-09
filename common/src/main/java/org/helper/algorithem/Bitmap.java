package org.helper.algorithem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 位图法解决问题:1.很大的数据排序;2.很大的数据中判断重复;3.很大的数据中判断是否存在
 * 要知道的:
 * 1.new int一个数组,用数组中每个int的1位或n位存储数据的状态,数据状态不能太多.
 * 2.一般做法,先遍历一遍获得大数中的最大值和最小值,还要考虑负数的情况.
 * 3.java中一个int占4字节,long占8字节.
 * 4.2^32bit=512M,可以表示42亿个数;2^10=1024;1MB=8*1024*1024,830w
 * <p>
 * <p>
 * 1、在2.5亿个整数中找出不重复的整数，注，内存不足以容纳这2.5亿个整数
 * 解法一：采用2-Bitmap（每个数分配2bit，00表示不存在，01表示出现一次，10表示多次，11无意义）进行，
 * 共需内存2^32 * 2 bit=1 GB内存，还可以接受。然后扫描这2.5亿个整数，查看Bitmap中相对应位，如果是00变01，01变10，10保持不变。
 * 所描完事后，查看bitmap，把对应位是01的整数输出即可。
 * 解法二：先找出2.5个数中的最大值和最小值(eg:1-123456789),然后切分成多份(eg:2份),
 * 先用位图排序(1-61728395(中位数))找出不重复的,然后再排序(61728395-123456789)找出不重复的.
 * 2、给40亿个不重复的unsigned int的整数，没排过序的，然后再给一个数，如何快速判断这个数是否在那40亿个数当中？
 * 解法一：可以用位图/Bitmap的方法，申请512M的内存，一个bit位代表一个unsigned int值。读入40亿个数，设置相应的bit位，
 * 读入要查询的数，查看相应bit位是否为1，为1表示存在，为0表示不存在。
 */
public class Bitmap {
    private static final Logger logger = LoggerFactory.getLogger(Bitmap.class);

    private static int INT_BIT = 8;

    private static int N = 100;
    private static int[] bitmap = new int[N];

    /**
     * n>>3表示n/8取整;
     * n & 7表示n % 8;
     * ~(1 << (n % 8))表示将其中一位置0,其他置1
     * @param n
     */
    private void set0(int n) {
        bitmap[n >> 3] &= ~(1 << (n % 8));
    }

    private void set1(int n) {
        bitmap[n >> 3] |= 1 << (n & 7);
    }

    private int is0Or1(int n) {
        return bitmap[n >> 3] & (1 << (n & 7));
    }
}
