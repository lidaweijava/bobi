package com.dream.bobi.controller;


import com.dream.bobi.commons.api.LoginRequest;
import com.dream.bobi.commons.entity.DayCount;
import com.dream.bobi.commons.entity.TransactionLog;
import com.dream.bobi.commons.entity.User;
import com.dream.bobi.commons.mapper.TransactionLogMapper;
import com.dream.bobi.support.BizException;
import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.api.UserApi;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.manage.UserService;
import com.dream.bobi.commons.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController extends BaseApiService implements UserApi {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    public Map<String, Object> findUser(Long userId) {
        try {
            UserEntity user = userService.getUserById(userId);
            return setResultSuccessData(user);
        }catch (BizException e){
            log.error("findUser error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("findUser error ",e);
            return setSystemError();
        }
    }

    @Override
    public Map<String, Object> register(@RequestBody UserEntity userEntity) {
//        if (StringUtils.isEmpty(userEntity.getUserName())){
//            return setResultParameterError(MsgCode.SYS_USER_NAME_NOT_NULL);
//        }
        if (StringUtils.isEmpty(userEntity.getPassword())){
            return setResultParameterError(MsgCode.SYS_PASSWORD_NOT_NULL);
        }

        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEntity.getPhone());
        if (!matcher.matches()) {
            return setResultParameterError(MsgCode.SYS_REGISTER_PHONE_NOT_VALID);
        }
        UserEntity user = userService.findUser(userEntity.getPhone());
        if (StringUtils.isEmpty(userEntity.getUserName())){
            userEntity.setUserName("user"+userEntity.getPhone());
        }
        if (null != user){
            return setResultError(MsgCode.SYS_USER_IS_EXIT);
        }
        try{
            userService.register(userEntity);
            return setResultSuccess();
        }catch (Exception e){
            log.error("register error ",e);
            return setResultError(MsgCode.SYS_REGISTER_FAIL);
        }
    }

    @Override
    public Map<String, Object> login(@RequestBody UserEntity userEntity) {
        if (StringUtils.isEmpty(userEntity.getPhone())){
            return setResultParameterError(MsgCode.SYS_PHONE_NOT_NULL);
        }
        if (StringUtils.isEmpty(userEntity.getPassword())){
            return setResultParameterError(MsgCode.SYS_PASSWORD_NOT_NULL);
        }
        try {
            String token = userService.login(userEntity);
            return setResultSuccessData(token);
        }catch (BizException e){
            log.error("login error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("login error ",e);
            return setSystemError();
        }
    }


    @Override
    public Map<String, Object> loginWithCode(@RequestBody LoginRequest loginRequest) {
        if (StringUtils.isEmpty(loginRequest.getPhone())){
            return setResultParameterError(MsgCode.SYS_PHONE_NOT_NULL);
        }
        if (StringUtils.isEmpty(loginRequest.getCode())){
            return setResultParameterError(MsgCode.VERIFY_CODE_NOT_NULL);
        }
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginRequest.getPhone());
        if (!matcher.matches()) {
            return setResultParameterError(MsgCode.SYS_REGISTER_PHONE_NOT_VALID);
        }
        try {
            String token = userService.loginWithCode(loginRequest);
            return setResultSuccessData(token);
        }catch (BizException e){
            log.error("loginWithCode error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("loginWithCode error ",e);
            return setSystemError();
        }
    }

    @Override
    public Map<String, Object> loginWithAppleToken(@RequestBody LoginRequest loginRequest) {
        if(StringUtils.isBlank(loginRequest.getAppleToken())){
            return setResultError(MsgCode.SYS_PARAM_ERROR);
        }
        try {
            String token = userService.loginWithAppleToken(loginRequest.getAppleToken());
            return setResultSuccessData(token);
        }catch (BizException e){
            log.error("loginWithCode error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("loginWithCode error ",e);
            return setSystemError();
        }
    }

    @Override
    public Map<String, Object> removeUser(@RequestParam("token") String token) {
        if (StringUtils.isEmpty(token)){
            return setResultParameterError(MsgCode.SYS_TOKEN_NOT_NULL);
        }
        try {
            UserEntity user = userService.getUser(token);
            userService.deleteUser(user.getId());
            userService.removeLoginToken(token);
            return setResultSuccessData(null);
        }catch (BizException e){
            log.error("removeUser error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("removeUser error ",e);
            return setSystemError();
        }
    }


    @Override
    public Map<String, Object> getUser(@RequestParam("token") String token) {
        if (StringUtils.isEmpty(token)){
            return setResultParameterError(MsgCode.SYS_TOKEN_NOT_NULL);
        }
        try {
            UserEntity user = userService.getUser(token);
            return setResultSuccessData(user);
        }catch (BizException e){
            log.error("getUser failed ", e);
            return setResultError(e.getMsgCode().getCode(),e.getMsgCode().getMessage());
        }catch (Exception e){
            log.error("getUser error ",e);
            return setResultError(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> recharge(Long userId, Long money) {
        if (userId == null ||  userId ==0 ||money== null ||money==0){
            return setResultParameterError(MsgCode.SYS_PARAM_ERROR);
        }
        try {
            Long newMoney = userService.recharge(userId, money);
            return setResultSuccessData(newMoney);
        }catch (BizException e){
            log.error("recharge failed ", e);
            return setResultError(e.getMsgCode().getCode(),e.getMsgCode().getMessage());
        }catch (Exception e){
            log.error("recharge error ",e);
            return setResultError(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> deduct(Long userId, Long money) {
        if (userId == null ||  userId ==0 ||money== null ||money==0){
            return setResultParameterError(MsgCode.SYS_PARAM_ERROR);
        }
        try {
            Long newMoney = userService.deduct(userId, money);
            return setResultSuccessData(newMoney);
        }catch (BizException e){
            log.error("deduct failed", e);
            return setResultError(e.getMsgCode().getCode(),e.getMsgCode().getMessage());
        }catch (Exception e){
            log.error("deduct error ",e);
            return setResultError(e.getMessage());
        }
    }


    @Autowired
    TransactionLogMapper transactionLogMapper;

    @GetMapping("/transLogs")
    public Map<String,Object> transactionLogs(@RequestParam String token) {
        try {
            UserEntity user = userService.getUser(token);
            Example example = new Example(TransactionLog.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",user.getId());
            example.setOrderByClause("id desc limit 50");
            List<TransactionLog> transactionLogs = transactionLogMapper.selectByExample(example);
            return setResultSuccessData(transactionLogs);
        }catch (BizException e){
            log.error("dayCountByMonth error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("dayCountByMonth error ",e);
            return setSystemError();
        }
    }

    @GetMapping("/isVip")
    public Map<String,Object> isVip(@RequestParam String token) {
        try {
            UserEntity user = userService.getUser(token);
            Boolean vip = userService.isVip(user.getId());
            if(vip && user.getVipEndTime().getTime()< System.currentTimeMillis()){
                return setResultSuccessData(true);
            }else{
                return setResultSuccessData(false);
            }
        }catch (BizException e){
            log.error("isVip error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("isVip error ",e);
            return setSystemError();
        }
    }

}
