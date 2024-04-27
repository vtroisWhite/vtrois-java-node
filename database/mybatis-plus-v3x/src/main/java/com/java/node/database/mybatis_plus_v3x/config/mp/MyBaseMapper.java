package com.java.node.database.mybatis_plus_v3x.config.mp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义sql
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {

    int saveBatchAllColumn(List<T> list);

    int updateAllColumnById(@Param(Constants.ENTITY) T entity);

    List<T> listByUuid(Serializable id);
}
