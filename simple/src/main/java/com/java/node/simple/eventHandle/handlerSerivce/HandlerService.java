package com.java.node.simple.eventHandle.handlerSerivce;

import com.java.node.simple.eventHandle.dto.EventParam;
import com.java.node.simple.eventHandle.dto.HandleResult;

/**
 * 事件处理父类
 */
public interface HandlerService {
    /**
     * 获取处理器信息，用于打印日志
     *
     * @return
     */
    HandlerEnum getHandleInfo();

    /**
     * 是否匹配此处理器
     *
     * @param dto
     * @return
     */
    boolean isMatch(EventParam dto);

    /**
     * 数据处理
     *
     * @param dto
     */
    HandleResult proceed(EventParam dto);
}
