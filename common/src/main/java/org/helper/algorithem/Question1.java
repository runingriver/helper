package org.helper.algorithem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hzz 18-3-2
 */
public class Question1 {
    private static final Logger logger = LoggerFactory.getLogger(Question1.class);

    /**
     * 最大差值,股票最大收益
     */
    public static int getDis(int[] A, int n) {
        int max = 0;
        int soFarMin = A[0];
        for (int i = 1; i < n; i++) {
            if (soFarMin > A[i]) {
                soFarMin = A[i];
            } else {
                max = Math.max(max, A[i] - soFarMin);
            }
        }
        return max;
    }

    /**
     * 股票只允许进行两次买卖,一次买入并且卖后,才能进行第二次买卖
     * 1.从中间点切分,分别计算两边的买卖最大收益;然后,将切分点进行向两边移动
     * 2.如下的解答方式
     */
    public static int maxProfit(int[] prices, int n) {
        int fbegin = Integer.MIN_VALUE, fend = 0, sbegin = Integer.MIN_VALUE, send = 0;
        for (int i = 0; i < n; i++) {
            if (prices[i] < fbegin) {
                fbegin = prices[i];
            } else if (prices[i] - fbegin > fend) {
                fend = prices[i] - fbegin;
            }
            if (fend - prices[i] > sbegin) {
                sbegin = fend - prices[i];
            } else if (prices[i] + sbegin > send) {
                send = prices[i] + sbegin;
            }
        }
        return send;
    }

    /**
     * 股票只允许进行两次买卖,一次买入并且卖后,才能进行第二次买卖
     * 上述第二中方式,即从中间点切分,分别计算两边,然后将中间点向左右移动
     * 所以可以直接,从1开始向n-1移动
     */
    public static int maxProfit2(int[] prices, int n) {
        int max1 = 0, max2 = 0, max = 0;
        for (int i = 2; i < n; i++) {
            max1 = getMaxForOnce(prices, 0, i);
            max2 = getMaxForOnce(prices, i, n);
            max = Math.max(max, max1 + max2);
        }
        return max;
    }

    /**
     * 动态转移方程: dp[i]={dp[i-1],a[i] - min}
     */
    public static int getMaxForOnce(int[] a, int s, int e) {
        int max = 0;
        int soFarMin = a[s];
        for (int i = s; i < e; i++) {
            if (soFarMin > a[i]) {
                soFarMin = a[i];
            }
            max = Math.max(max, a[i] - soFarMin);
        }
        return max;
    }

    public static void main(String[] args) {
        int[] tab = new int[]{4, 8, 11, 7, 89, 2, 15, 33, 5};
        int dis = maxProfit2(tab, tab.length);
        logger.info("result:{}", dis);
    }
}
