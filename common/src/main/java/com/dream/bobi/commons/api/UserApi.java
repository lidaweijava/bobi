package com.dream.bobi.commons.api;

import com.dream.bobi.commons.entity.UserEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@RequestMapping("/user")
public interface UserApi {

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserEntity userEntity);

    /**
     * 描述：登录成功后，生成对应的 Token，作为 Key，将用户 userId 作为 value 存放在 redis 中，返回 Token 给客户端
     * 登陆功能
     *
     * @return
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserEntity userEntity);
    /**
     * 描述：登录成功后，生成对应的 Token，作为 Key，将用户 userId 作为 value 存放在 redis 中，返回 Token 给客户端
     * 登陆功能
     *
     * @return
     */
    @PostMapping("/loginWithCode")
    public Map<String, Object> loginWithCode(@RequestBody LoginRequest loginRequest);

    @PostMapping("/remove")
    Map<String, Object> removeUser(@RequestParam("token") String token);
    /**
     * 使用 Token 查找用户信息
     *
     * @param token
     * @return
     */
    @PostMapping("/getUserByToken")
    public Map<String, Object> getUser(@RequestParam("token") String token);

    /**
     * 使用userId查找用户信息
     *
     * @param userId
     * @return
     */
    @PostMapping("/findUserById")
    public Map<String, Object> findUser(@RequestParam("userId") Long userId);
    /**
     * 充值
     *
     * @param userId
     * @return
     */
    @PostMapping("/recharge")
    public Map<String, Object> recharge(@RequestParam("userId") Long userId,@RequestParam("userId") Long money);/**
     * 充值
     *
     * @param userId
     * @return
     */
    @PostMapping("/deduct")
    public Map<String, Object> deduct(@RequestParam("userId") Long userId,@RequestParam("userId") Long money);
}
