package com.java.node.web.paramConverter.param;

import com.java.node.web.paramConverter.config.ObjectFormat;
import lombok.Data;

/**
 * 测试 复杂类型转换
 */
@Data
public class ObejctFormatTestParam {


    private String id;

    @ObjectFormat
    private InnerParam data;

    @Data
    public static class InnerParam {

        private String id;

        private String name;
    }
}
