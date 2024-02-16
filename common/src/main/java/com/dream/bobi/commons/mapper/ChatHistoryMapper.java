package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.ChatHistory;
import com.dream.bobi.commons.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;


@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory>, ExampleMapper<ChatHistory>, InsertListMapper<ChatHistory> {

}