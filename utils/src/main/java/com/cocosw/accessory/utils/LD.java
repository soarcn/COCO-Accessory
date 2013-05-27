package com.cocosw.accessory.utils;

/**
 * Levenshtein distance最先是由俄国科学家Vladimir
 * Levenshtein在1965年发明，用他的名字命名。不会拼读，可以叫它edit distance（编辑距离）。 Levenshtein
 * distance可以用来：
 * <p/>
 * Spell checking(拼写检查) Speech recognition(语句识别) DNA analysis(DNA分析) Plagiarism
 * detection(抄袭检测) LD用m*n的矩阵存储距离值。算法大概过程：
 * <p/>
 * str1或str2的长度为0返回另一个字符串的长度。 初始化(n+1)*(m+1)的矩阵d，并让第一行和列的值从0开始增长。
 * 扫描两字符串（n*m级的），如果：str1[i] ==
 * str2[j]，用temp记录它，为0。否则temp记为1。然后在矩阵d[i][j]赋于d[i-1][j]+1
 * 、d[i][j-1]+1、d[i-1][j-1]+temp三者的最小值。 扫描完后，返回矩阵的最后一个值即d[n][m]
 */
public class LD {

    /**
     * 计算矢量距离 Levenshtein Distance(LD)
     *
     * @param str1 str1
     * @param str2 str2
     * @return ld
     */
    public static int ld(String str1, String str2) {
        // Distance
        int[][] d;
        int n = str1.length();
        int m = str2.length();
        int i; // iterate str1
        int j; // iterate str2
        char ch1; // str1
        char ch2; // str2
        int temp;
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) {
            ch1 = str1.charAt(i - 1);
            // match str2
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }

                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
                        + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }

    /**
     * 计算相似度
     *
     * @param str1 str1
     * @param str2 str2
     * @return sim
     */
    public static double sim(String str1, String str2) {
        int ld = ld(str1, str2);
        return 1 - (double) ld / Math.max(str1.length(), str2.length());
    }

}
