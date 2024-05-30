package com.dream.bobi.manage;

import com.dream.bobi.commons.api.LoginRequest;
import com.dream.bobi.commons.entity.UserEntity;



public interface UserService {

    /**
     * 功能描述:(注册服务)
     *
     * @param userEntity
     */
    public void register(UserEntity userEntity);

    /**
     * 密码加密
     *
     * @param phone
     * @param password
     * @return
     */
    public String md5PassSalt(String phone, String password);

    public String login(UserEntity userEntity);

    public UserEntity getUser(String token);
    public void deleteUser(Long userId);
    public void removeLoginToken(String token);
    public UserEntity getUserById(Long userId);

    public UserEntity findUser(String phone);

    public Long recharge(Long userId,Long money);
    Long deduct(Long userId, Long money);

    String loginWithCode(LoginRequest loginRequest);

    String loginWithAppleToken(String appleToken);

    Boolean isVip(Long userId);
}
