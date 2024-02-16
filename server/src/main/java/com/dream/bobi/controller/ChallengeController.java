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
    public Map<String, Object> challengeDetail(@PathVariable Long challengeRecordId) {
        try {
            ChallengeRecord challengeRecord = challengeRecordMapper.selectByPrimaryKey(challengeRecordId);
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
            }
            ChallengeRecord challengeRecord = new ChallengeRecord();
            challengeRecord.setChallengeId(challenge.getId());
            challengeRecord.setCategory(challenge.getCategory());
            challengeRecord.setStatus(2);
            challengeRecord.setDialogCount(0);
            challengeRecord.setFailCause(1);
            challengeRecord.setUserId(user.getId());
            challengeRecord.setChallengeTime(new Date());
            int id = challengeRecordMapper.insertSelective(challengeRecord);
            return setResultSuccessData(id);
        } catch (BizException e) {
            log.error("init error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("init error ", e);
            return setSystemError();
        }
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestParam String token, @RequestParam String text, @RequestParam Long challengeRecordId) {
        try {
            UserEntity user = userService.getUser(token);
            ChallengeRecord challengeRecord = challengeRecordMapper.selectByPrimaryKey(challengeRecordId);
            Challenge challenge = challengeMapper.selectByPrimaryKey(challengeRecord.getChallengeId());
            Map<String, Object> reqBody = new HashMap<>();
            Map<String, Object> messages = new HashMap<>();
            messages.put("sender_type", "USER");
            messages.put("sender_name", "主人");
            messages.put("text", text);
            reqBody.put("model", "abab5.5-chat");
            reqBody.put("tokens_to_generate", 1034);
            reqBody.put("temperature", 0.01);
            reqBody.put("top_p", 0.95);
            Map<String, String> constraint = new HashMap<>();
            constraint.put("sender_type", "BOT");
            constraint.put("sender_name", "智能宠物助理");
            reqBody.put("reply_constraints", constraint);
            List<Map> botSetting = new ArrayList<>();
            Map<String, String> sett = new HashMap<>();
            sett.put("bot_name", "智能宠物助理");
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
                    botMessage.put("sender_name", "智能宠物助理");
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
                    return setResultError(MsgCode.CHALLENGE_FAILED);
                }
                if (succFound.isPresent()) {
                    // save succ result
                    saveSuccessResult(challengeRecordId, lastMessage.size());
                    return setResultSuccess(MsgCode.CHALLENGE_SUCCESS);
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