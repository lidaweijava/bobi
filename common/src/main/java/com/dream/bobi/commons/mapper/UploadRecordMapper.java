package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.UploadRecord;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;

@Mapper
public interface UploadRecordMapper extends BaseMapper<UploadRecord>, ExampleMapper<UploadRecord>, InsertListMapper<UploadRecord> {

}