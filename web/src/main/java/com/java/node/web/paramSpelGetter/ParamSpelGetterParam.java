package com.java.node.web.paramSpelGetter;

import lombok.Data;

import java.util.List;

@Data
public class ParamSpelGetterParam {

    private Integer id;

    private String name;

    private ParamSpelGetterParam next;

    private List<ParamSpelGetterParam> nextList;
}
