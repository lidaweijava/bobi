package com.dream.bobi.commons.constants;

public interface Constants {

    //用户会话保存90天
    Long USER_TOKEN_TERM_VALIDITY = 60 * 60 * 24 * 90L;
    int WEBUSER_COOKIE_TOKEN_TERMVALIDITY = 1000 * 60 * 60 * 24 * 90;
    String USER_TOKEN = "token";
    String USER_SESSION_OPENID= "openId";
    String USER_SOURCE_QQ = "QQ";



}
