package com.java.node.database.double_write.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * DDL
 *
 * @Description
 */
@Data
@Accessors(chain = true)
@Table(name = "`test_table_1`")
public class TestTable1 {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

    @Column(name = "`int_1`")
    private Integer int1;

    @Column(name = "`long_1`")
    private Long long1;

    @Column(name = "`bit_1`")
    private Long bit1;

    @Column(name = "`date_1`")
    private Date date1;

    @Column(name = "`date_time_1`")
    private Date dateTime1;

    @Column(name = "`decimal_1`")
    private BigDecimal decimal1;

    @Column(name = "`text_1`")
    private String text1;
}