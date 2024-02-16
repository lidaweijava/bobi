package com.dream.bobi.manage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class TokenService {
    public static final Cache<String, Long> userTokens = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(24, TimeUnit.HOURS)
                //移除通知
                .build();

}
