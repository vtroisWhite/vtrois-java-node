package com.java.node.web.paramConverter.param;

import com.java.node.web.paramConverter.config.TimestampToDate;
import lombok.Data;

import java.util.Date;

/**
 * 测试时间戳转date
 */
@Data
public class TimestampToDateParam2 {

    private String id;

    @TimestampToDate
    private Date date;
}
