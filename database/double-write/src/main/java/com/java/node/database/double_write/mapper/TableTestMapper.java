package com.java.node.database.double_write.mapper;

import com.java.node.database.double_write.entity.TestTable1;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description
 */
@Repository
public interface TableTestMapper extends Mapper<TestTable1> {

    int insertOne(TestTable1 data);

    /**
     * 批量新增且自定义id
     *
     * @param list
     * @return
     */
    int insertMultiple(List<TestTable1> list);

    /**
     * 批量新增且不自定义id
     *
     * @param list
     * @return
     */
    int insertMultipleNoId(List<TestTable1> list);
}
