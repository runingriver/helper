package org.helper.common.javap;

/**
 * @author hzz 18-2-7
 */
public class VolatileDemo {
    private static long a;
    private static int b;
    private volatile static int c;

    public static void main(String[] args) {
        set();
        setAgain();
    }

    public static void set() {
        a = 1;
        b = 1;
        c = 1;
    }

    public static void setAgain() {
        if (c == 1) {
            a = 2;
            b = 2;
        }
    }
}
