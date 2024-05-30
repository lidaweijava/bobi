package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.Code;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;

@Mapper
public interface CodeMapper extends BaseMapper<Code>, ExampleMapper<Code>, InsertListMapper<Code> {
}
