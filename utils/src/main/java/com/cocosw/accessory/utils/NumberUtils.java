package com.cocosw.accessory.utils;

/**
 * Format number utlis
 */
public class NumberUtils {

    /**
     * Generate formatted number like
     * 12030 -> 12+k
     * 130222 -> 13+w
     * TODO i18n
     *
     * @param num
     * @return
     */
    public static String getBeautyNum(long num) {
        if (num < 1000)
            return String.valueOf(num);
        if (num < 99999)
            return (num / 1000) + "+千";
        if (num < 9999999)
            return (num / 10000) + "+万";
        return (num / 10000000) + "+千万";
    }
}
