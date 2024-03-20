package com.dream.bobi.commons.utils;


public class NumberUtil {
    public static boolean isBitZero(long number, int index) {
        return (number>>index &1)==0;
    }

    public static void main(String[] args) {
        String binaryString = Long.toBinaryString(40);
        System.out.println(binaryString);
    }

}
