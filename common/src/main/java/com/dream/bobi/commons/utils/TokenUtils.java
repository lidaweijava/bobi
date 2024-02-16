package com.dream.bobi.commons.utils;

import java.util.UUID;


public class TokenUtils {

    public static String getToken(){
        return UUID.randomUUID().toString();
    }

}
