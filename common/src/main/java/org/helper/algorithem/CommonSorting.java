package org.helper.algorithem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 九大排序算法
 */
public class CommonSorting {
    public static final Logger logger = LoggerFactory.getLogger(CommonSorting.class);

    public static void main(String[] args) {
        int[] a = new int[]{5, 4, 3, 2, 1};
        directInsertSort(a);
        logger.info("{}", a);

        int[] a1 = new int[]{5, 4};
        directInsertSort(a1);
        logger.info("{}", a1);

        int[] a2 = new int[]{5};
        directInsertSort(a2);
        logger.info("{}", a2);
    }

    /**
     * 直接插入,保证前面的是有序的,后面的是无序的!
     * @param a 待排序数组
     */
    public static void directInsertSort(int[] a) {
        int n = a.length;
        int i, j, t;
        for (i = 1; i < n; i++) {
            t = a[i];
            j = i - 1;
            while (j >= 0 && a[j] > t) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = t;
        }
    }

    /**
     * 选择排序
     * @param a 待排序数组
     */
    public static void selectSort(int[] a) {
        int n = a.length;
        int i, j, minIndex;
        for (i = 1; i < n; i++) {
            minIndex = i;
            for (j = i - 1; j >= 0 && a[j] > a[j + 1]; j--) {
                minIndex = j;
            }
            if (minIndex != i) {
                a[minIndex] ^= a[i] ^= a[minIndex] ^= a[i];
            }
        }
    }

    /**
     * 冒泡排序,done用于优化当部分有序时的情况
     * @param a 待排序数组
     */
    public static void bubbleSort(int[] a) {
        int n = a.length;
        int i, j;
        boolean done = false;
        for (i = 1; i < n && !done; i++) {
            done = true;
            for (j = 0; j < n - i; j++) {
                if (a[j] > a[j + 1]) {
                    a[j] ^= a[j + 1];
                    a[j + 1] ^= a[j];
                    a[j] ^= a[j + 1];
                    done = false;
                }
            }
        }
    }

    /**
     * 二分插入排序
     * @param a 待排序数组
     */
    public static void binary_insert_sort(int[] a) {

    }

    /**
     * 希尔排序
     * @param a 待排序数组
     */
    public static void shell_sort(int[] a) {

    }

    /**
     * 快排
     * @param a 待排序数组
     */
    public static void quick_sort(int[] a) {

    }

}
