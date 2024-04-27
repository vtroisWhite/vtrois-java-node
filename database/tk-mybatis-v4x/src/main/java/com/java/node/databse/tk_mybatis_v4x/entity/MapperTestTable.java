package com.java.node.databse.tk_mybatis_v4x.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Data
@Accessors(chain = true)
@Table(name = "`mapper_test_table`")
public class MapperTestTable {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "`table_id`")
    private Integer tableId;

    @Column(name = "`uuid`")
    private String uuid;

    @Column(name = "`name`")
    private String name;

    /**
     * 计数器
     */
    @Column(name = "`counter`")
    private Integer counter;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`", insertable = false, updatable = false)
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "`update_time`", insertable = false, updatable = false)
    private Date updateTime;
}