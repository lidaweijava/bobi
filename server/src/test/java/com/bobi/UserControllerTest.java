package com.bobi;

import com.dream.bobi.Application;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
public class UserControllerTest {

   @Resource
    private UserController userController;

   @Test
    public void login(){
       UserEntity userEntity = new UserEntity();
       userEntity.setPhone("18888889999");
       userEntity.setPassword("123456");
       Map<String, Object> map = userController.login(userEntity);
       System.out.println(map);
   }
}