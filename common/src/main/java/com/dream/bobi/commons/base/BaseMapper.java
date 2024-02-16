package com.dream.bobi.commons.base;

import tk.mybatis.mapper.common.base.insert.InsertSelectiveMapper;
import tk.mybatis.mapper.common.base.select.SelectByPrimaryKeyMapper;
import tk.mybatis.mapper.common.base.select.SelectMapper;
import tk.mybatis.mapper.common.base.select.SelectOneMapper;
import tk.mybatis.mapper.common.base.update.UpdateByPrimaryKeySelectiveMapper;
import tk.mybatis.mapper.common.example.SelectByExampleMapper;
import tk.mybatis.mapper.common.example.SelectCountByExampleMapper;
import tk.mybatis.mapper.common.example.UpdateByExampleSelectiveMapper;
import tk.mybatis.mapper.common.rowbounds.SelectByExampleRowBoundsMapper;

public interface BaseMapper<T> extends InsertSelectiveMapper<T>, UpdateByExampleSelectiveMapper<T>, UpdateByPrimaryKeySelectiveMapper<T>,
        SelectOneMapper<T>, SelectByPrimaryKeyMapper<T>, SelectMapper<T>, SelectByExampleMapper<T>, SelectByExampleRowBoundsMapper<T>,
        SelectCountByExampleMapper<T> {}