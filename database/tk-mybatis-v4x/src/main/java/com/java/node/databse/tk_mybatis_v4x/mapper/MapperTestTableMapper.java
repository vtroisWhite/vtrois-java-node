package com.java.node.databse.tk_mybatis_v4x.mapper;

import com.java.node.databse.tk_mybatis_v4x.entity.MapperTestTable;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;


@Repository
public interface MapperTestTableMapper extends Mapper<MapperTestTable>,
        InsertListMapper<MapperTestTable>,
        IdsMapper<MapperTestTable> {

}