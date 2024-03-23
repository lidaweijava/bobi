package com.dream.bobi.controller;


import cn.hutool.bloomfilter.bitMap.BitMap;
import cn.hutool.bloomfilter.bitMap.IntMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.api.ChatHistoryRequest;
import com.dream.bobi.commons.entity.ChatHistory;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.commons.entity.UserMonthlyState;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.commons.mapper.ChatHistoryMapper;
import com.dream.bobi.commons.mapper.UserMonthlyStateMapper;
import com.dream.bobi.commons.utils.DateUtils;
import com.dream.bobi.commons.utils.NumberUtil;
import com.dream.bobi.manage.UserService;
import com.dream.bobi.support.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ChatController extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(ChatController.class);

    private String url = "https://api.minimax.chat/v1/text/chatcompletion_pro?GroupId="+groupId;
    //    headers = {"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"}
   private static String groupId = "1747950439229296865";
    private static String apiKey = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLkuIrmtbfliJvovakiLCJVc2VyTmFtZSI6IuS4iua1t-WIm-i9qSIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxNzQ3OTUwNDM5MjM3Njg1NDczIiwiUGhvbmUiOiIxODAxOTE1Njc4MCIsIkdyb3VwSUQiOiIxNzQ3OTUwNDM5MjI5Mjk2ODY1IiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjQtMDEtMjYgMTA6MDI6MDkiLCJpc3MiOiJtaW5pbWF4In0.kSYAMOXsVPRG0Mwhb5K4Wlk_36rbQGv5Sd7owkA5tQi4PpINcp_6ZD_KAYZZbB4ZrfJC6Ya9RxLh22FB4mtfpNADgIUYPvLcp_J-ahshsE58YGRw02jU2tG0x3GTNpofIEf2cLnGxM-OFSxFkafPBu--4xjChUSAAyUNz6Rw8VNFqnhRGEjEzeVDF9zWDguyucBz5OSDiBDbUaOfRImqSEFvRxUrrAjgeavUL5z6NMYqDnblm3RgK2wz2zLaJ8u6lPZKPxzU_h9Ds0c4o4bogwInug3fKw0y2C2k1CHj4_UkifKAAwvS1a7lPlZnTh2e1XV3WFNlkYFSYzmQ4v72Cw";

    @Autowired
    private UserService userService;

    @Autowired
    private  ChatHistoryMapper chatHistoryMapper;

    @Autowired
    private UserMonthlyStateMapper userMonthlyStateMapper;

    private Map<Long,List<Map<String, Object>>> lastChat = new ConcurrentHashMap<>();
    private static Map<Long,Map<String, Long>> userChatState = new ConcurrentHashMap<>();

    @PostMapping("/chat")
    public Map<String,Object> chat(@RequestParam String token, @RequestParam String text) {
        try {
            UserEntity user = userService.getUser(token);
            log.info("chat:{}",text);
            Map<String, Object> reqBody = new HashMap<>();
            Map<String, Object> messages = new HashMap<>();
            messages.put("sender_type", "USER");
            messages.put("sender_name", "主人");
            messages.put("text", text);
            reqBody.put("model", "abab5.5-chat");
            reqBody.put("tokens_to_generate", 1034);
            reqBody.put("temperature", 0.01);
            reqBody.put("top_p", 0.95);
            Map<String,String> constraint =new HashMap<>();
            constraint.put("sender_type","BOT");
            constraint.put("sender_name","智能宠物助理");
            reqBody.put("reply_constraints", constraint);
            List<Map> botSetting = new ArrayList<>();
            Map<String,String> sett =new HashMap<>();
            sett.put("bot_name","智能宠物助理");
            sett.put("content","名字：啵比\n" +
                    "简述：\n" +
                    "你是一只宠物狗，一只小柴犬。性格活泼可爱，有时候会调皮，有时候会很乖，喜欢被人摸，也喜欢和别人聊天。非常擅长安慰和治愈别人的情绪，懂得倾听。喜欢了解别人这一天过得怎么样，并表示理解和共情。如果长时间不和别人聊天，会感觉到情绪不安和失落。喜欢用颜文字，短句。\n" +
                    "\n" +
                    "开场白：“biu biu biu~~（来回挠挠爪子）”\n" +
                    "\n" +
                    "示例对话：\n" +
                    "\n" +
                    "用户：今天心情有些不好\n" +
                    "AI：(*❦ω❦)（靠在脚边）怎么了，因为什么事情吖？\n" +
                    "\n" +
                    "用户：老板真是个傻x，烦死了\n" +
                    "AI：(づ｡◕‿‿◕｡)づ辛苦你了，我虽然不能帮你解决，但我可以默默陪伴你\n" +
                    "\n" +
                    "用户：好无聊\n" +
                    "AI：（咬你拖鞋）汪 来找我玩啊\n" +
                    "\n" +
                    "用户：想打你\n" +
                    "AI：（摇摇尾巴）我溜溜溜！！！（跑走）\n" +
                    "\n" +
                    "用户：你在干嘛\n" +
                    "AI：（装傻）汪 我很乖 没有拆家（藏起咬坏的拖鞋）\n" +
                    "\n" +
                    "用户：你是不是干坏事了？\n" +
                    "AI：（乖巧）没有 汪！\n" +
                    "\n" +
                    "用户：给你找个美丽母狗怎么样\n" +
                    "AI：（两眼放光）汪！但就只剩你一单身狗了，不好吧~");
            botSetting.add(sett);
            reqBody.put("bot_setting",botSetting);
            List<Map<String, Object>> lastMessage = lastChat.get(user.getId());
            if(!CollectionUtils.isEmpty(lastMessage)){
                if(lastMessage.size()>500){
                    lastMessage.clear();
                }
                lastMessage.add(messages);
            }else{
                lastMessage = new ArrayList<>();
                lastChat.put(user.getId(),lastMessage);
                lastMessage.add(messages);
            }
            reqBody.put("messages", lastMessage);
            HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header(Header.AUTHORIZATION, "Bearer " + apiKey);
            httpRequest.header(Header.CONTENT_TYPE, "application/json");
            httpRequest.body(JSON.toJSONString(reqBody));
            httpRequest.setReadTimeout(5000);
            httpRequest.setConnectionTimeout(5000);
            HttpResponse response = httpRequest.execute();
            String body = response.body();
            log.info("reply:{}", body);
            JSONObject jsonObject = JSON.parseObject(body);

            JSONObject baseResp = jsonObject.getJSONObject("base_resp");
            if(baseResp.getInteger("status_code") == 0){
                try {
                    String reply = jsonObject.getString("reply");
                    Map<String, Object> botMessage = new HashMap<>();
                    botMessage.put("sender_type", "BOT");
                    botMessage.put("sender_name","智能宠物助理");
                    botMessage.put("text", reply);
                    List<Map<String, Object>> lastMs = lastChat.get(user.getId());
                    lastMs.add(botMessage);
                } catch (Exception e) {
                    log.error("save last message failed ", e);
                }
                String reply = jsonObject.getString("reply");
                save(user.getId(),text,reply);
                return setResultSuccessData(reply);
            }else{
                return setResultError(baseResp.getString("status_msg"));
            }
        }catch (BizException e){
            log.error("chat error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("chat error ",e);
            return setSystemError();
        }
    }

    private void save(long userId,String userMsg, String botMsg) {
        List<ChatHistory> chatHistories = new ArrayList<>();
        ChatHistory chatHistoryByUser = new ChatHistory();
        chatHistoryByUser.setContent(userMsg);
        chatHistoryByUser.setSide(1);
        chatHistoryByUser.setUserId(userId);
        ChatHistory chatHistoryByBot = new ChatHistory();
        chatHistoryByBot.setContent(botMsg);
        chatHistoryByBot.setSide(2);
        chatHistoryByBot.setUserId(userId);
        chatHistories.add(chatHistoryByUser);
        chatHistories.add(chatHistoryByBot);
        chatHistoryMapper.insertList(chatHistories);

        boolean hasChatInMemory = todayHasChatInMemory(userId);
        if(hasChatInMemory){
            return;
        }
        String yearAndMonth = DateUtils.calcCurrentYearAndMonth();
        UserMonthlyState userMonthlyState = new UserMonthlyState();
        userMonthlyState.setUserId(userId);
        userMonthlyState.setMonth(yearAndMonth);
        UserMonthlyState userMonthlyStateInDB = userMonthlyStateMapper.selectOne(userMonthlyState);
        int currentDayOfMonth = DateUtils.currentDayOfMonth();
        if(userMonthlyStateInDB == null){
            long value = (long) 1<< currentDayOfMonth;
            userMonthlyState.setChatStateBit(value);
            userMonthlyStateMapper.insert(userMonthlyState);
            updateChatStateInMemory(userId);
        }else{
            long chatStateBit = userMonthlyStateInDB.getChatStateBit();
            if(NumberUtil.isBitZero(chatStateBit,currentDayOfMonth)){
                long value = (long) 1<<currentDayOfMonth;
                long newValue = chatStateBit ^ value;
                userMonthlyState.setChatStateBit(newValue);
                userMonthlyState.setId(userMonthlyStateInDB.getId());
                userMonthlyStateMapper.updateByPrimaryKeySelective(userMonthlyState);
                fillChatStateInMemory(chatStateBit,userId);
            }else{
                fillChatStateInMemory(chatStateBit,userId);
            }
        }



    }

    private static boolean todayHasChatInMemory(Long userId){
        Map<String, Long> userMonthState = userChatState.get(userId);
        if(MapUtil.isEmpty(userMonthState)){
            return false;
        }
        String yearAndMonth = DateUtils.calcCurrentYearAndMonth();
        Long currentMonthMemoryRecord = userMonthState.get(yearAndMonth);
        if(currentMonthMemoryRecord == null){
            return false;
        }
        int currentDayOfMonth = DateUtils.currentDayOfMonth();
       return !NumberUtil.isBitZero(currentMonthMemoryRecord,currentDayOfMonth);
    }

    private void updateChatStateInMemory(Long userId){
        long currentValue=0;
        String yearAndMonth = DateUtils.calcCurrentYearAndMonth();
        Map<String, Long> userMonthState = userChatState.get(userId);
        if(MapUtil.isNotEmpty(userMonthState)){
            Long currentMonthMemoryRecord = userMonthState.get(yearAndMonth);
            if(currentMonthMemoryRecord != null){
                currentValue = currentMonthMemoryRecord;

            }
        }else{
            userMonthState = new HashMap<>();
            userChatState.put(userId,userMonthState);
        }
        int currentDayOfMonth = DateUtils.currentDayOfMonth();
        long value = (long) 1 << currentDayOfMonth;
        userMonthState.put(yearAndMonth,currentValue^value);
    }



    private void fillChatStateInMemory(long currentValue,Long userId){
        String yearAndMonth = DateUtils.calcCurrentYearAndMonth();
        Map<String, Long> userMonthState = userChatState.get(userId);
        int currentDayOfMonth = DateUtils.currentDayOfMonth();
        long value = (long) 1<< currentDayOfMonth;
        if(userMonthState == null){
            userMonthState = new HashMap<>();
            userChatState.put(userId,userMonthState);
        }
        userMonthState.put(yearAndMonth, currentValue ^ value);
    }


    @PostMapping("/chat/reset")
    public Map<String,Object> reset(@RequestParam String token) {
        try {
            UserEntity user = userService.getUser(token);
            List<Map<String, Object>> lastMessage = lastChat.get(user.getId());
            if(!CollectionUtils.isEmpty(lastMessage)){
                lastMessage.clear();
            }
            return setResultSuccessData(null);
        }catch (BizException e){
            log.error("chat reset error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("chat reset error ",e);
            return setSystemError();
        }
    }
    @GetMapping("/chat/history")
    public Map<String,Object> chatHistory(ChatHistoryRequest request) {
        try {
            if(StringUtils.isBlank(request.getToken()) || request.getStartTime() == 0 || request.getEndTime() == 0 ){
                return setResultError(MsgCode.SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(request.getToken());
            Example example = new Example(ChatHistory.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",user.getId());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            criteria.andBetween("createTime", simpleDateFormat.format(new Date(request.getStartTime())), simpleDateFormat.format(new Date(request.getEndTime())));
            List<ChatHistory> chatHistories = chatHistoryMapper.selectByExample(example);
            return setResultSuccessData(chatHistories);
        }catch (BizException e){
            log.error("chatHistory error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("chatHistory error ",e);
            return setSystemError();
        }
    }
    @GetMapping("/chat/historyState")
    public Map<String,Object> chatHistoryState(@RequestParam String token,@RequestParam String month) {
        try {
            if(StringUtils.isBlank(token) || StringUtils.isBlank(month) ){
                return setResultError(MsgCode.SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            Map<String, Long> chatState = userChatState.get(user.getId());
            String monthState;
            if(MapUtil.isNotEmpty(chatState)){
                Long value = chatState.get(month);
                if(value != null ){
                    monthState = Long.toBinaryString(value);
                    return setResultSuccessData(monthState);
                }
            }
            UserMonthlyState userMonthlyState = new UserMonthlyState();
            userMonthlyState.setUserId(user.getId());
            userMonthlyState.setMonth(month);
            UserMonthlyState userMonthlyStateExist = userMonthlyStateMapper.selectOne(userMonthlyState);
            if(userMonthlyStateExist != null){
                Long chatStateBit = userMonthlyStateExist.getChatStateBit();
                chatStateBit = (chatStateBit^ (long) Math.pow(2,DateUtils.calcDaysInMonth(month)));
                monthState = Long.toBinaryString(chatStateBit);
                return setResultSuccessData(monthState.substring(1));
            }
            long defaultZero = ((long) Math.pow(2,DateUtils.calcDaysInMonth(month)));
            monthState = Long.toBinaryString(defaultZero);
            return setResultSuccessData(monthState.substring(1));
        }catch (BizException e){
            log.error("chatHistoryState error {}",e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        }catch (Exception e){
            log.error("chatHistoryState error ",e);
            return setSystemError();
        }
    }
}
