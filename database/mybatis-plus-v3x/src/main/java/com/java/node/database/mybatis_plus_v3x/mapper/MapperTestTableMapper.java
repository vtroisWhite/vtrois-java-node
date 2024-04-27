package com.java.node.database.mybatis_plus_v3x.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.java.node.database.mybatis_plus_v3x.config.mp.MyBaseMapper;
import com.java.node.database.mybatis_plus_v3x.entity.MapperTestTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 */
@Mapper
public interface MapperTestTableMapper extends MyBaseMapper<MapperTestTable> {

    /**
     * 分页查询
     *
     * @return
     */
    IPage<MapperTestTable> queryList(@Param("page") IPage<MapperTestTable> page, @Param("param") MapperTestTable param);

}
