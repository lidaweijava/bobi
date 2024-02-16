package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.Config;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;


@Mapper
public interface ConfigMapper extends BaseMapper<Config>, ExampleMapper<Config> {

}