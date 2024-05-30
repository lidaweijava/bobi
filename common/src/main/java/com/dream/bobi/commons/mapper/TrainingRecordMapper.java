package com.dream.bobi.commons.mapper;

import com.dream.bobi.commons.entity.DayCount;
import com.dream.bobi.commons.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ExampleMapper;

import java.util.List;


@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord>, ExampleMapper<TrainingRecord>, InsertListMapper<TrainingRecord> {

    List<Integer> selectMonthList(@Param("userId") Long userId);
    List<DayCount> sumTakesGroupByDayPerMonth(@Param("userId") Long userId, @Param("month") Integer month);
}