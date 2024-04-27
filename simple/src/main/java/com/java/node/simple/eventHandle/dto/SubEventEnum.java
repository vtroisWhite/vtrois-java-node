package com.java.node.simple.eventHandle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 二级事件枚举
 */
@AllArgsConstructor
public enum SubEventEnum {

    /**
     * app 注册 登录 活跃
     */
    APP(Arrays.asList(EventEnum.LOGIN, EventEnum.REGISTER, EventEnum.ACTIVE)),
    /**
     * h5 注册 登录 活跃
     */
    H5(Arrays.asList(EventEnum.LOGIN, EventEnum.REGISTER, EventEnum.ACTIVE)),
    /**
     * 正面
     */
    BACK(Arrays.asList(EventEnum.VERIFY_ID_CARD)),
    /**
     * 反面
     */
    FRONT(Arrays.asList(EventEnum.VERIFY_ID_CARD)),

    NONE(Collections.emptyList()),
    ;
    private static final Map<EventEnum, List<SubEventEnum>> map =
            Arrays.stream(EventEnum.values()).collect(Collectors.toMap(Function.identity(), event -> {
                List<SubEventEnum> collect = Arrays.stream(SubEventEnum.values()).filter(x -> x.getParentEventList().contains(event)).collect(Collectors.toList());
                return collect.isEmpty() ? Collections.singletonList(NONE) : collect;
            }));
    @Getter
    private final List<EventEnum> parentEventList;

    public static List<SubEventEnum> getByParentEvent(EventEnum eventEnum) {
        return map.get(eventEnum);
    }
}
