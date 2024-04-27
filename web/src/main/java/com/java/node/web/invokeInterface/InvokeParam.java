package com.java.node.web.invokeInterface;

import lombok.Data;

import java.util.List;

/**
 * @Description
 */
@Data
public class InvokeParam {

    private String className;

    private String methodName;

    private List<Arg> argList;

    @Data
    public static class Arg {

        private String classType;

        private Object value;
    }
}

