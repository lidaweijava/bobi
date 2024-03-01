package com.dream.bobi.controller;


import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.entity.Challenge;
import com.dream.bobi.commons.entity.ChallengeRecord;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.commons.mapper.ChallengeMapper;
import com.dream.bobi.commons.mapper.ChallengeRecordMapper;
import com.dream.bobi.manage.UserService;
import com.dream.bobi.support.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/challenge")
public class ChallengeController extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(ChallengeController.class);

    private String url = "https://api.minimax.chat/v1/text/chatcompletion_pro?GroupId=" + groupId;
    //    headers = {"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"}
    private static String groupId = "1747950439229296865";
    private static String apiKey = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLkuIrmtbfliJvovakiLCJVc2VyTmFtZSI6IuS4iua1t-WIm-i9qSIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxNzQ3OTUwNDM5MjM3Njg1NDczIiwiUGhvbmUiOiIxODAxOTE1Njc4MCIsIkdyb3VwSUQiOiIxNzQ3OTUwNDM5MjI5Mjk2ODY1IiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjQtMDEtMjYgMTA6MDI6MDkiLCJpc3MiOiJtaW5pbWF4In0.kSYAMOXsVPRG0Mwhb5K4Wlk_36rbQGv5Sd7owkA5tQi4PpINcp_6ZD_KAYZZbB4ZrfJC6Ya9RxLh22FB4mtfpNADgIUYPvLcp_J-ahshsE58YGRw02jU2tG0x3GTNpofIEf2cLnGxM-OFSxFkafPBu--4xjChUSAAyUNz6Rw8VNFqnhRGEjEzeVDF9zWDguyucBz5OSDiBDbUaOfRImqSEFvRxUrrAjgeavUL5z6NMYqDnblm3RgK2wz2zLaJ8u6lPZKPxzU_h9Ds0c4o4bogwInug3fKw0y2C2k1CHj4_UkifKAAwvS1a7lPlZnTh2e1XV3WFNlkYFSYzmQ4v72Cw";

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeMapper challengeMapper;
    @Autowired
    private ChallengeRecordMapper challengeRecordMapper;

    private Map<Long, List<Map<String, Object>>> lastChat = new ConcurrentHashMap<>();
    private Map<Long, Long> session = new ConcurrentHashMap<>();


    @GetMapping("/config/{challengeId}")
    public Map<String, Object> challengeConfig(@PathVariable Integer challengeId) {
        try {
            Challenge challenge = challengeMapper.selectByPrimaryKey(challengeId);
            if (challenge == null) {
                return setResultError(MsgCode.CHALLENGE_CONFIG_NOT_FOUND);
            }
            return setResultSuccessData(challenge);
        } catch (BizException e) {
            log.error("challengeConfig error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("challengeConfig error ", e);
            return setSystemError();
        }
    }
    @PutMapping("/config")
    public Map<String, Object> challengeSave(Challenge challenge) {
        try {
            challengeMapper.insertSelective(challenge);
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("challengeSave error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("challengeSave error ", e);
            return setSystemError();
        }
    }
    @DeleteMapping("/config/{challengeId}")
    public Map<String, Object> challengeDel(@PathVariable Integer challengeId) {
        try {
            challengeMapper.deleteByPrimaryKey(challengeId);
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("challengeDel error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("challengeDel error ", e);
            return setSystemError();
        }
    }

    @GetMapping("/config/list")
    public Map<String, Object> challengeConfigs() {
        try {
            List<Challenge> challenges = challengeMapper.selectAll();
            if (CollectionUtils.isEmpty(challenges)) {
                return setResultError(MsgCode.CHALLENGE_CONFIG_NOT_FOUND);
            }
            return setResultSuccessData(challenges);
        } catch (BizException e) {
            log.error("challengeConfigs error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("challengeConfigs error ", e);
            return setSystemError();
        }
    }

    @GetMapping("/record/{challengeRecordId}")
    public Map<String, Object> challengeDetail(@RequestParam String token,@PathVariable Long challengeRecordId) {
        try {
            UserEntity user = userService.getUser(token);
            ChallengeRecord challengeRecord = challengeRecordMapper.selectByPrimaryKey(challengeRecordId);
            if(!Objects.equals(challengeRecord.getUserId(), user.getId())){
                return setResultError(MsgCode.CHALLENGE_NOT_FOUND);
            }
            return setResultSuccessData(challengeRecord);
        } catch (BizException e) {
            log.error("challengeDetail error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("challengeDetail error ", e);
            return setSystemError();
        }
    }

    @PostMapping("/init")
    public Map<String, Object> init(@RequestParam String token, @RequestParam Integer challengeId) {
        try {
            UserEntity user = userService.getUser(token);
            List<Map<String, Object>> lastMessage = lastChat.get(user.getId());
            Challenge challenge = challengeMapper.selectByPrimaryKey(challengeId);
            if (challenge == null) {
                return setResultError(MsgCode.CHALLENGE_CONFIG_NOT_FOUND);
            }
            if (!CollectionUtils.isEmpty(lastMessage)) {
                lastMessage.clear();
                session.remove(user.getId());
            }
            ChallengeRecord challengeRecord = new ChallengeRecord();
            challengeRecord.setChallengeId(challenge.getId());
            challengeRecord.setCategory(challenge.getCategory());
            challengeRecord.setStatus(0);
            challengeRecord.setDialogCount(0);
            challengeRecord.setFailCause(0);
            challengeRecord.setUserId(user.getId());
            challengeRecord.setChallengeTime(new Date());
            challengeRecordMapper.insertSelective(challengeRecord);
            return setResultSuccessData(challengeRecord.getId());
        } catch (BizException e) {
            log.error("init error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("init error ", e);
            return setSystemError();
        }
    }


    @PostMapping("/stop")
    public Map<String, Object> stop(@RequestParam String token, @RequestParam Long challengeRecordId) {
        try {
            UserEntity user = userService.getUser(token);
            List<Map<String, Object>> lastMessage = lastChat.get(user.getId());
            ChallengeRecord exited = challengeRecordMapper.selectByPrimaryKey(challengeRecordId);
            if (exited == null) {
                return setResultError(MsgCode.CHALLENGE_NOT_FOUND);
            }
            if(!Objects.equals(user.getId(), exited.getUserId())){
                return setResultError(MsgCode.CHALLENGE_NOT_FOUND);
            }
            if (!CollectionUtils.isEmpty(lastMessage)) {
                lastMessage.clear();
                session.remove(user.getId());
            }
            ChallengeRecord challengeRecord = new ChallengeRecord();
            challengeRecord.setId(challengeRecordId);
            challengeRecord.setStatus(1);
            challengeRecord.setFailCause(2);
            challengeRecordMapper.updateByPrimaryKeySelective(challengeRecord);
            return setResultSuccessData(challengeRecord.getId());
        } catch (BizException e) {
            log.error("stop error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("stop error ", e);
            return setSystemError();
        }
    }

    @PostMapping("/heartbeat")
    public Map<String, Object> heartbeat(@RequestParam String token,@RequestParam Long challengeRecordId) {
        try {
            UserEntity user = userService.getUser(token);
            challengeRecordCheck(challengeRecordId);
            sessionExpireCheckAndSaveIfExpire(user.getId(),challengeRecordId);
            sessionAdd(user.getId());
            return setResultSuccess();
        } catch (BizException e) {
            log.error("heartbeat error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("heartbeat error ", e);
            return setSystemError();
        }
    }

    private void sessionAdd(Long userId) {
        session.put(userId,System.currentTimeMillis());
    }

    private ChallengeRecord challengeRecordCheck(Long challengeRecordId) {
        ChallengeRecord challengeRecord = challengeRecordMapper.selectByPrimaryKey(challengeRecordId);
        if(challengeRecord == null){
            throw new BizException(MsgCode.CHALLENGE_NOT_FOUND);
        }
        if(challengeRecord.getStatus() != 0){
            throw new BizException(MsgCode.CHALLENGE_IS_OVER);
        }
        return challengeRecord;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestParam String token, @RequestParam String text, @RequestParam Long challengeRecordId) {
        try {
            UserEntity user = userService.getUser(token);
            log.info("chat:{}",text);
            ChallengeRecord challengeRecord = challengeRecordCheck(challengeRecordId);
            sessionExpireCheckAndSaveIfExpire(user.getId(),challengeRecordId);
            Challenge challenge = challengeMapper.selectByPrimaryKey(challengeRecord.getChallengeId());
            Map<String, Object> reqBody = new HashMap<>();
            Map<String, Object> messages = new HashMap<>();
            messages.put("sender_type", "USER");
            messages.put("sender_name", challenge.getUserName());
            messages.put("text", text);
            reqBody.put("model", "abab5.5-chat");
            reqBody.put("tokens_to_generate", 1034);
            reqBody.put("temperature", 0.01);
            reqBody.put("top_p", 0.95);
            Map<String, String> constraint = new HashMap<>();
            constraint.put("sender_type", "BOT");
            constraint.put("sender_name", challenge.getBotName());
            reqBody.put("reply_constraints", constraint);
            List<Map> botSetting = new ArrayList<>();
            Map<String, String> sett = new HashMap<>();
            sett.put("bot_name", challenge.getBotName());
            sett.put("content", challenge.getAiDetail() + "\n" + challenge.getSampleDialog());
            botSetting.add(sett);
            reqBody.put("bot_setting", botSetting);
            List<Map<String, Object>> lastMessage = lastChat.get(user.getId());
            if (!CollectionUtils.isEmpty(lastMessage)) {
                lastMessage.add(messages);
            } else {
                lastMessage = new ArrayList<>();
                lastChat.put(user.getId(), lastMessage);
                lastMessage.add(messages);
            }
            sessionAdd(user.getId());
            reqBody.put("messages", lastMessage);
            HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header(Header.AUTHORIZATION, "Bearer " + apiKey);
            httpRequest.header(Header.CONTENT_TYPE, "application/json");
            httpRequest.body(JSON.toJSONString(reqBody));
            HttpResponse response = httpRequest.execute();
            String body = response.body();
            log.info("reply:{}", body);
            JSONObject jsonObject = JSON.parseObject(body);
            JSONObject baseResp = jsonObject.getJSONObject("base_resp");
            if (baseResp.getInteger("status_code") == 0) {
                try {
                    String reply = jsonObject.getString("reply");
                    Map<String, Object> botMessage = new HashMap<>();
                    botMessage.put("sender_type", "BOT");
                    botMessage.put("sender_name", challenge.getBotName());
                    botMessage.put("text", reply);
                    List<Map<String, Object>> lastMs = lastChat.get(user.getId());
                    lastMs.add(botMessage);
                } catch (Exception e) {
                    log.error("save last message failed ", e);
                }
                String reply = jsonObject.getString("reply");
                String[] succKeywords = challenge.getSuccessKeyword().split(",");
                String[] failKeywords = challenge.getFailKeyword().split(",");
                Optional<String> succFound = Arrays.stream(succKeywords).filter(reply::contains).findAny();
                Optional<String> failFound = Arrays.stream(failKeywords).filter(reply::contains).findAny();
                if (failFound.isPresent()) {
                    saveFailResult(challengeRecordId, lastMessage.size());
                    for(String sk:succKeywords){
                        reply = reply.replaceAll(sk,"");
                    }
                    return setResultSuccessWithData(MsgCode.CHALLENGE_FAILED,reply);
                }
                if (succFound.isPresent()) {
                    // save succ result
                    saveSuccessResult(challengeRecordId, lastMessage.size());
                    for(String sk:succKeywords){
                        reply = reply.replaceAll(sk,"");
                    }
                    return setResultSuccessWithData(MsgCode.CHALLENGE_SUCCESS,reply);
                } else {
                    if (lastMessage.size() >= challenge.getMaxTimes()) {
                        // save fail result
                        saveFailResultForExceedMaxTimes(challengeRecordId, lastMessage.size());
                        return setResultError(MsgCode.CHALLENGE_EXCEED_MAX_TIMES);
                    }
                    //continue
                }
                return setResultSuccessData(reply);
            } else {
                return setResultError(baseResp.getString("status_msg"));
            }
        } catch (BizException e) {
            log.error("chat error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("chat error ", e);
            return setSystemError();
        }
    }

    private void sessionExpireCheckAndSaveIfExpire(Long userId,Long challengeRecordId) {
        Long lastSessionTime = session.get(userId);
        if(lastSessionTime == null ||lastSessionTime == 0){
            return;
        }
        if(System.currentTimeMillis()-lastSessionTime >90*1000L){
            ChallengeRecord challengeRecord = new ChallengeRecord();
            challengeRecord.setId(challengeRecordId);
            challengeRecord.setStatus(3);
            challengeRecordMapper.updateByPrimaryKeySelective(challengeRecord);
            throw new BizException(MsgCode.SESSION_TIMEOUT);
        }
    }

    private void saveFailResult(Long recordId, int challengeTimes) {
        ChallengeRecord challengeRecord = new ChallengeRecord();
        challengeRecord.setId(recordId);
        challengeRecord.setStatus(2);
        challengeRecord.setDialogCount(challengeTimes);
        challengeRecord.setFailCause(2);
        challengeRecordMapper.updateByPrimaryKeySelective(challengeRecord);
    }

    private void saveFailResultForExceedMaxTimes(long recordId, int challengeTimes) {
        ChallengeRecord challengeRecord = new ChallengeRecord();
        challengeRecord.setId(recordId);
        challengeRecord.setStatus(2);
        challengeRecord.setDialogCount(challengeTimes);
        challengeRecord.setFailCause(1);
        challengeRecordMapper.updateByPrimaryKeySelective(challengeRecord);
    }

    private void saveSuccessResult(long recordId, int challengeTimes) {
        ChallengeRecord challengeRecord = new ChallengeRecord();
        challengeRecord.setId(recordId);
        challengeRecord.setGrade(challengeTimes < 10 ? 'S' : 'A');
        challengeRecord.setStatus(1);
        challengeRecord.setDialogCount(challengeTimes);
        challengeRecordMapper.updateByPrimaryKeySelective(challengeRecord);
    }

}
