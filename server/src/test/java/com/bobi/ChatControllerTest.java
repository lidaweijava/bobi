package com.bobi;

import com.dream.bobi.Application;
import com.dream.bobi.commons.utils.DateUtils;
import com.dream.bobi.controller.ChatController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
public class ChatControllerTest {

    @Resource
    private ChatController chatController;

    @Test
    public void historyState(){
        Map<String,Object> map = chatController.chatHistoryState("57596b45-7677-4bde-b4d8-9ac80d58dc3a","202404");
        System.out.println(map);
        long defaultZero = ((long) Math.pow(2, DateUtils.calcDaysInMonth("202404")));
        String monthState = Long.toBinaryString(defaultZero);
        String result = monthState.substring(1);
        System.out.println(result);
    }
}
