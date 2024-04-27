package com.java.node.web.paramConverter.param;

import com.java.node.web.paramConverter.config.ObjectFormat;
import lombok.Data;

import java.util.List;

/**
 * 测试 复杂类型转换
 */
@Data
public class CollectionTestParam {


    private String id;

    @ObjectFormat(objectType = ObjectFormat.ObjectType.ARRAY)
    private List<InnerParam> list;

    @Data
    public static class InnerParam {

        private String id;

        private String name;

        private List<Integer> cfgList;

        private InnerParam2 nameDetail;
    }

    @Data
    public static class InnerParam2 {

        private String fName;

        private String lName;

    }

}
