package com.java.node.web.paramConverter.param;

import lombok.Data;

import java.util.List;

/**
 * 测试 复杂类型转换
 */
@Data
public class ConverterTestParam1 {


    private String id;

    private List<InnerParam> list;


    @Data
    public static class InnerParam {

        private String id;

        private String name;
    }
}
