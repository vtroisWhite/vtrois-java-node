package com.java.node.database.multi_datasource.mapper.db2;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Description
 */
@Repository
public interface DB2TableTestMapper {

    @Select("SELECT DATABASE()")
    String queryOne();
}
