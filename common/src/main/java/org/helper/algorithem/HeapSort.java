package org.helper.algorithem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 堆排序
 * 将数组模拟成完全二叉树:
 * 1.设根节点index为1
 * 2.跟节点的子节点index为:2*i和2*i+1
 * 小顶堆满足的特点:
 * 1.根节点比左右子节点都小
 * 完全二叉树:
 * 1.叶节点只能出现在最下层和次下层，并且最下面一层的结点都集中在该层最左边的若干位置的二叉树
 *
 * @author hzz 18-3-1
 */
public class HeapSort {
    private static final Logger logger = LoggerFactory.getLogger(HeapSort.class);

    /**
     * 将数组中以root为根节点,长度为size的部分,将最小值调整到根上
     */
    public static void createHeap(int[] tab, int root, int size) {
        int i = root, j = 2 * i + 1;
        boolean finished = false;
        int temp = tab[i];
        while (j < size && !finished) {
            if (j < size - 1 && tab[j + 1] < tab[j]) {
                j++;
            }

            if (temp <= tab[j]) {
                finished = true;
            } else {
                tab[i] = tab[j];
                i = j;
                j = 2 * i + 1;
            }
        }
        tab[i] = temp;
    }

    public static void heapSort(int[] tab) {
        int size = tab.length;
        for (int i = size / 2 - 1; i >= 0; i--) {
            createHeap(tab, i, size);
        }
        //logger.info("step0:{}", tab);
        for (int i = 1; i < size; i++) {
            int temp = tab[0];
            tab[0] = tab[size - i];
            tab[size - i] = temp;
            //logger.info("step{}:{}", i, tab);
            createHeap(tab, 0, size - i);
        }
    }

    public static void main(String[] args) {
        int[] tab = new int[]{90, 40, 37, 22, 12, 28, 10, 11, 9};
        heapSort(tab);
        logger.info("sort:{}", tab);

        //tab = new int[]{9, 42};
        //heapSort(tab);
        //logger.info("sort:{}", tab);

        Random random = new Random(1000);
        for (int i = 0; i < 100; i++) {
            int size = random.nextInt(30);
            int[] table = new int[size];
            for (int j = 0; j < size; j++) {
                table[j] = random.nextInt(1000);
            }
            heapSort(table);
            logger.info("{}:{}", isAscOrder(table), table);

        }
    }

    private static boolean isAscOrder(int[] table) {
        int size = table.length;
        for (int i = 0; i < size - 1; i++) {
            if (table[i] < table[i + 1]) {
                logger.error("error sort:{}", table);
                return false;
            }
        }
        return true;
    }
}
