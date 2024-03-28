package com.dream.bobi.commons.utils;


public class NumberUtil {
    public static boolean isBitZero(long number, int index) {
        return (number>>(index-1) &1)==0;
    }


}
