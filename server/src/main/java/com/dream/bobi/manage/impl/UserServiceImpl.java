package com.dream.bobi.manage.impl;

import com.alibaba.fastjson.JSON;
import com.dream.bobi.commons.api.LoginRequest;
import com.dream.bobi.commons.entity.TransactionLog;
import com.dream.bobi.commons.mapper.TransactionLogMapper;
import com.dream.bobi.manage.CacheService;
import com.dream.bobi.support.BizException;
import com.dream.bobi.commons.entity.User;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.commons.mapper.UserMapper;
import com.dream.bobi.commons.utils.DateUtils;
import com.dream.bobi.commons.utils.MD5Util;
import com.dream.bobi.commons.utils.TokenUtils;
import com.dream.bobi.manage.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final static Logger log = LoggerFactory.getLogger(DateUtils.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionLogMapper transactionLogMapper;


    @Override
    public void register(UserEntity userEntity) {
      try {
          String phone = userEntity.getPhone();
          String password = userEntity.getPassword();
          userEntity.setPassword(md5PassSalt(phone, password));
          User user = new User();
          user.setUserName(userEntity.getUserName());
          user.setPhone(userEntity.getPhone());
          user.setPassword(md5PassSalt(phone, password));
          user.setToken(TokenUtils.getToken());
          user.setEmail(userEntity.getEmail());
          userMapper.insertSelective(user);
      }catch (Exception e){
          log.error("save user failed:{}", JSON.toJSONString(userEntity),e);
          throw new BizException(MsgCode.SYS_REGISTER_FAIL);
      }
    }


    @Override
    public String md5PassSalt(String phone, String password) {
        return MD5Util.MD5(phone + password);
    }

    /**
     * 登录查找并生成 Token
     *
     * @param userEntity
     * @return
     */
    @Override
    public String login(UserEntity userEntity) {
        //往数据库进行查找数据
        String phone = userEntity.getPhone();
        String password = userEntity.getPassword();
        String newPassword = md5PassSalt(phone, password);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone",phone);
        criteria.andEqualTo("password",newPassword);
        List<User> users = userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)) {
            throw new BizException(MsgCode.SYS_ACCOUNT_ERROR);
        }
        User existUser = new User();
        User user = users.get(0);
        existUser.setId(user.getId());
        existUser.setLastLoginTime(DateUtils.getTimestamp());
        userMapper.updateByPrimaryKeySelective(existUser);
        return setLoginToken(user.getId());
    }

    @Override
    public String loginWithCode(LoginRequest loginRequest) {
        User user = new User();
        user.setPhone(loginRequest.getPhone());
        User userInDB = userMapper.selectOne(user);
        if(userInDB == null){
            return createUserAndLogin(loginRequest);
        }
        return setLoginToken(userInDB.getId());
    }


    @Override
    public String loginWithAppleToken(String appleToken) {
        User user = new User();
        user.setPhone(null);
        user.setToken(appleToken);
        User userInDB = userMapper.selectOne(user);
        if(userInDB == null){
            return createUserWithAppleTokenAndLogin(appleToken);
        }
        return setLoginToken(userInDB.getId());
    }

    private String createUserWithAppleTokenAndLogin(String appleToken) {
        User user = new User();
        user.setPhone(null);
        user.setPassword("");
        user.setEmail("");
        user.setToken(appleToken);
        user.setUserName("user_apple_"+appleToken.substring(8));
        userMapper.insertSelective(user);
        return setLoginToken(user.getId());
    }

    private String createUserAndLogin(LoginRequest loginRequest) {
        User user = new User();
        user.setPhone(loginRequest.getPhone());
        user.setPassword(md5PassSalt(loginRequest.getPhone(), "123456"));
        user.setEmail("");
        user.setToken("");
        user.setUserName("bobiUser_"+loginRequest.getPhone().substring(8));
        userMapper.insertSelective(user);
        return setLoginToken(user.getId());
    }

    @Override
    public UserEntity getUser(String token) {
        Long userIdObj = CacheService.userTokens.getIfPresent(token);
        log.info("UserServiceImpl get user, the token = {}, the userIdObj = {}", token, userIdObj);
        if (userIdObj == null){
            throw new BizException(MsgCode.SYS_INVALID_LOGIN);
        }
        User user = userMapper.selectByPrimaryKey(userIdObj);
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        userEntity.setPassword(null);
        return userEntity;
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteByPrimaryKey(userId);
    }

    private String setLoginToken(Long userId) {
        //生成对应的token
        String token = UUID.randomUUID().toString();
        //key为自定义令牌,用户的userId作作为value 存放在redis中
        CacheService.userTokens.put(token,userId);
        return token;
    }
    public void removeLoginToken(String token) {
        //生成对应的token
        //key为自定义令牌,用户的userId作作为value 存放在redis中
        CacheService.userTokens.put(token,0L);
    }

    @Override
    public UserEntity findUser(String phone) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone",phone);
        List<User> users = userMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(users)){
            return null;
        }else{
            User user = users.get(0);
            UserEntity userEntity = new UserEntity();
            userEntity.setUserName(user.getUserName());
            userEntity.setMoney(user.getMoney());
            userEntity.setLevel(user.getLevel());
            userEntity.setLastLoginTime(user.getLastLoginTime());
            return userEntity;
        }
    }

    @Override
    public UserEntity getUserById(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            throw new BizException(MsgCode.SYS_USER_IS_NOT_EXIT);
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        return userEntity;
    }

    @Override
    public Long recharge(Long userId, Long money) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            throw new BizException(MsgCode.SYS_USER_IS_NOT_EXIT);
        }
        if(money<=0){
            throw new BizException(MsgCode.PARAM_ERROR_MONEY_MUST_GREATER_THAN_0);
        }
        Long rows = userMapper.increaseMoney(userId, money);

        if(rows<1){
            throw new BizException(MsgCode.SYS_RECHARGE_FAILED);
        }
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setUserId(user.getId());
        transactionLog.setBalance(Math.toIntExact(user.getMoney()+money));
        transactionLog.setChange(Math.toIntExact(money));
        transactionLog.setTransTime(new Date());
        transactionLogMapper.insertSelective(transactionLog);
        User afterUser = userMapper.selectByPrimaryKey(userId);
        return afterUser.getMoney();
    }
    @Override
    @Transactional
    public Long deduct(Long userId, Long money) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            throw new BizException(MsgCode.SYS_USER_IS_NOT_EXIT);
        }
        if(money<=0){
            throw new BizException(MsgCode.PARAM_ERROR_MONEY_MUST_GREATER_THAN_0);
        }
        Long rows = userMapper.deductMoney(userId, money);
        if(rows<1){
            throw new BizException(MsgCode.SYS_DEDUCT_FAILED);
        }
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setUserId(user.getId());
        transactionLog.setBalance(Math.toIntExact(user.getMoney()-money));
        transactionLog.setChange(-Math.toIntExact(money));
        transactionLog.setTransTime(new Date());
        transactionLogMapper.insertSelective(transactionLog);
        User afterUser = userMapper.selectByPrimaryKey(userId);
        return afterUser.getMoney();
    }

    @Override
    public Boolean isVip(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            throw new BizException(MsgCode.SYS_USER_IS_NOT_EXIT);
        }
        return user.getVip() && user.getVipEndTime().after(new Date());
    }
}
