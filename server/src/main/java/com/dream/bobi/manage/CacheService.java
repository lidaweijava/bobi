package com.dream.bobi.manage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class CacheService {
    public static final Cache<String, Long> userTokens = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(24*30, TimeUnit.HOURS)
                //移除通知
                .build();
    public static final Cache<String, Object> configs = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(1, TimeUnit.HOURS)
                //移除通知
                .build();

}
