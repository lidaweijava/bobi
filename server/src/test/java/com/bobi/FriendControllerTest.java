package com.bobi;

import com.dream.bobi.Application;
import com.dream.bobi.commons.entity.ChatHistory;
import com.dream.bobi.commons.entity.InviteFriendRecord;
import com.dream.bobi.commons.enums.InviteFriendStatus;
import com.dream.bobi.commons.mapper.InviteFriendRecordMapper;
import com.dream.bobi.controller.FriendController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.dream.bobi.commons.constants.BaseApiConstants.HTTP_DATA_NAME;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
public class FriendControllerTest {

    @Resource
    private FriendController friendController;
    @Resource
    private InviteFriendRecordMapper inviteFriendRecordMapper;

    @Test
    public void encryptOrDecryptPasteContent(){
        Map<String, Object> value1 = friendController.encrypt("3efc64d9-0b0f-4a77-a368-0aa95812cdb7");
        Map<String, Object> data1 = (Map<String, Object>) value1.get(HTTP_DATA_NAME);
        Map<String, Object> value2 = friendController.decryptPasteContent(String.valueOf(data1.get("encryptUserId")));
        System.out.println();
    }

    @Test
    public void insert(){
        try {
            InviteFriendRecord record = new InviteFriendRecord();
            record.setStatus(InviteFriendStatus.INVITE.getStatus());
            record.setUserId(123L);
            record.setTargetUserId(456L);
            inviteFriendRecordMapper.insertSelective(record);
        }catch (Exception e) {
            System.out.println();
        }

    }

    @Test
    public void query(){
        try {
            Example example = new Example(InviteFriendRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("targetUserId",456L);
            criteria.andEqualTo("status", InviteFriendStatus.INVITE.getStatus());
            List<InviteFriendRecord> inviteFriendRecords = inviteFriendRecordMapper.selectByExample(example);
            System.out.println();
        }catch (Exception e) {
            System.out.println();
        }
    }

    @Test
    public void update(){
        try {
            InviteFriendRecord inviteFriendRecord = new InviteFriendRecord();
            inviteFriendRecord.setId(1L);
            inviteFriendRecord.setStatus(InviteFriendStatus.AGREE.getStatus());
            inviteFriendRecordMapper.updateByPrimaryKeySelective(inviteFriendRecord);
            System.out.println();
        }catch (Exception e) {
            System.out.println();
        }
    }


}
