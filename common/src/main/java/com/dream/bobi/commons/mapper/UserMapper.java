package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User>, ExampleMapper<User> {

    Long increaseMoney(@Param("userId") Long userId,@Param("money") Long money);
    Long deductMoney(@Param("userId") Long userId,@Param("money") Long money);
    List<User> selectByUidList(@Param("uidList") List<Long> uidList);
}