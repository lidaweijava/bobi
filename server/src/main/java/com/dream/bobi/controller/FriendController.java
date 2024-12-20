package com.dream.bobi.controller;

import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.entity.InviteFriendRecord;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.commons.enums.InviteFriendStatus;
import com.dream.bobi.commons.mapper.InviteFriendRecordMapper;
import com.dream.bobi.commons.utils.EncryptionUtil;
import com.dream.bobi.manage.UserService;
import com.dream.bobi.support.BizException;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dream.bobi.commons.enums.MsgCode.FRIEND_ENCRYPT_FAIL;
import static com.dream.bobi.commons.enums.MsgCode.SYS_PARAM_ERROR;

@RestController
public class FriendController extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(FriendController.class);

    private final static String ENTROPY_PASS_WORD = "entropy123QWE.";

    @Autowired
    private UserService userService;
    @Autowired
    private InviteFriendRecordMapper inviteFriendRecordMapper;

    /**
     * 复制口令, 对我的uid加密，生成字符串
     * @param token
     * @return
     */
    @PostMapping("/friend/encrypt")
    public Map<String, Object> encrypt(@RequestParam String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                log.error("FriendController encrypt token is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            if (user == null || user.getId() == null){
                log.error("FriendController encrypt parse fail, the token:{}", token);
                throw new BizException(FRIEND_ENCRYPT_FAIL);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("encryptUserId", EncryptionUtil.encryptWithAES(String.valueOf(user.getId()), ENTROPY_PASS_WORD));
            return setResultSuccessData(map);
        } catch (BizException e) {
            log.error("encrypt error {}, token:{}", token, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("encrypt error, the token:{}", token, e);
            return setSystemError();
        }
    }

    /**
     * 黏贴口令, 对我的uid加密，生成字符串
     * @param encryptUserId
     * @return
     */
    @GetMapping("friend/paste")
    public Map<String, Object> decryptPasteContent(@RequestParam String encryptUserId) {
        try {
            Map<String, Object> map = new HashMap<>();
            encryptUserId = encryptUserId.replaceAll(" ","+");
            String value = EncryptionUtil.decryptWithAES(encryptUserId, ENTROPY_PASS_WORD);
            if (StringUtils.isNullOrEmpty(value)) {
                log.error("FriendController decryptPasteContent parse fail, the encryptUserId:{}", encryptUserId);
                throw new BizException(FRIEND_ENCRYPT_FAIL);
            }
            Long userId = Long.parseLong(value);
            UserEntity user = userService.getUserById(userId);
            map.put("userId", userId);
            map.put("userName", user.getUserName());
            return setResultSuccessData(map);
        } catch (BizException e) {
            log.error("encrypt error {}, encryptUserId:{}", encryptUserId, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("encrypt error, the encryptUserId:{}", encryptUserId, e);
            return setSystemError();
        }
    }

    /**
     * 前端请求后端发起邀请好友接口（uid或者phoneNo）
     * @param token
     * @return
     */
    @PostMapping("/friend/invite/send")
    public Map<String, Object> inviteFriend(@RequestParam String token, @RequestParam(required = false) Long targetUserId
            , @RequestParam(required = false) String targetPhone) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                log.error("FriendController inviteFriend token is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            if (user == null || user.getId() == null){
                log.error("FriendController inviteFriend parse fail, the token:{}", token);
                throw new BizException(SYS_PARAM_ERROR);
            }
            if (StringUtils.isNullOrEmpty(targetPhone) && targetUserId == null) {
                throw new BizException(SYS_PARAM_ERROR);
            }

            //获取目标用户uid
            if (targetUserId == null) {
                UserEntity userEntity = userService.findUser(targetPhone);
                if (userEntity == null) {
                    throw new BizException(SYS_PARAM_ERROR);
                }
                targetUserId = userEntity.getId();
            }

            InviteFriendRecord record = new InviteFriendRecord();
            record.setStatus(InviteFriendStatus.INVITE.getStatus());
            record.setUserId(user.getId());
            record.setTargetUserId(targetUserId);
            inviteFriendRecordMapper.insertSelective(record);
            return setResultSuccess();
        } catch (BizException e) {
            log.error("encrypt error {}, token:{}", token, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("encrypt error, the token:{}", token, e);
            return setSystemError();
        }
    }

    /**
     * 后端查询自己的被邀请列表，返回uid和用户名列表给到前端
     * @param token
     * @return
     */
    @GetMapping("friend/beInvite/query")
    public Map<String, Object> getBeInviteList(@RequestParam String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                log.error("FriendController getBeInviteList token is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            if (user == null || user.getId() == null){
                log.error("FriendController getBeInviteList fail, the token:{}", token);
                throw new BizException(SYS_PARAM_ERROR);
            }

            Example example = new Example(InviteFriendRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("targetUserId",user.getId());
            criteria.andEqualTo("status", InviteFriendStatus.INVITE.getStatus());
            List<InviteFriendRecord> inviteFriendRecords = inviteFriendRecordMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(inviteFriendRecords)) {
                return setResultSuccess();
            }
            List<Long> userIdList = inviteFriendRecords.stream().map(InviteFriendRecord::getTargetUserId).collect(Collectors.toList());
            return setResultSuccessData(userService.getUserByIdList(userIdList));
        } catch (BizException e) {
            log.error("getBeInviteList error {}, token:{}", token, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("getBeInviteList error, the token:{}", token, e);
            return setSystemError();
        }
    }

    /**
     * 传给后端对方uid和自己的token，然后update关系
     * @param token
     * @return
     */
    @PostMapping("friend/invite/agree")
    public Map<String, Object> agreeInviteFriend(@RequestParam String token, @RequestParam Long inviteId) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                log.error("FriendController agreeInviteFriend token is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            if (user == null || user.getId() == null){
                log.error("FriendController agreeInviteFriend parse fail, the token:{}", token);
                throw new BizException(SYS_PARAM_ERROR);
            }

            Example example = new Example(InviteFriendRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",inviteId);
            criteria.andEqualTo("targetUserId",user.getId());
            criteria.andEqualTo("status", InviteFriendStatus.INVITE.getStatus());
            List<InviteFriendRecord> inviteFriendRecords = inviteFriendRecordMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(inviteFriendRecords)){
                return setResultSuccess();
            }
            //update记录
            InviteFriendRecord inviteFriendRecord = new InviteFriendRecord();
            inviteFriendRecord.setId(inviteFriendRecords.get(0).getId());
            inviteFriendRecord.setStatus(InviteFriendStatus.AGREE.getStatus());
            inviteFriendRecordMapper.updateByPrimaryKeySelective(inviteFriendRecord);
            return setResultSuccess();
        } catch (BizException e) {
            log.error("getBeInviteList error {}, token:{}", token, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("getBeInviteList error, the token:{}", token, e);
            return setSystemError();
        }
    }

    /**
     * 根据手机号查询是否是bobi用户
     * @param phone
     * @return
     */
    @GetMapping("/v1/user/check")
    public Map<String, Object> checkUser(@RequestParam String phone) {
        try {
            if (StringUtils.isNullOrEmpty(phone)) {
                log.error("FriendController checkUser phone is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.findUser(phone);
            Map<String, Object> map = new HashMap<>();
            if (user == null){
                map.put("userFlag", false);
            }else {
                map.put("userFlag", true);
            }
            return setResultSuccessData(map);
        } catch (BizException e) {
            log.error("encrypt error {}, phone:{}", phone, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("encrypt error, the phone:{}", phone, e);
            return setSystemError();
        }
    }

    /**
     * 我的bobi好友
     * @param token
     * @return
     */
    @GetMapping("friend/my/query")
    public Map<String, Object> getMyFriend(@RequestParam String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                log.error("FriendController getMyFriend token is null");
                throw new BizException(SYS_PARAM_ERROR);
            }
            UserEntity user = userService.getUser(token);
            if (user == null || user.getId() == null){
                log.error("FriendController getMyFriend fail, the token:{}", token);
                throw new BizException(SYS_PARAM_ERROR);
            }

            Example example = new Example(InviteFriendRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",user.getId());
            criteria.andEqualTo("status", InviteFriendStatus.AGREE.getStatus());
            List<InviteFriendRecord> inviteFriendRecords = inviteFriendRecordMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(inviteFriendRecords)) {
                return null;
            }
            List<Long> userIdList = inviteFriendRecords.stream().map(InviteFriendRecord::getTargetUserId).collect(Collectors.toList());
            return setResultSuccessData(userService.getUserByIdList(userIdList));
        } catch (BizException e) {
            log.error("getBeInviteList error {}, token:{}", token, e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("getBeInviteList error, the token:{}", token, e);
            return setSystemError();
        }
    }
}
