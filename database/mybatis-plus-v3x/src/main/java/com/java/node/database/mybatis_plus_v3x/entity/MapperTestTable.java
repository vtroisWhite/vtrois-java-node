package com.java.node.database.mybatis_plus_v3x.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("mapper_test_table")
public class MapperTestTable {

    @TableId(value = "table_id", type = IdType.AUTO)
    private Integer tableId;

    @TableField("uuid")
    private String uuid;

    @TableField("name")
    private String name;

    /**
     * 计数器
     */
    @TableField("counter")
    private Integer counter;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Date updateTime;
}
