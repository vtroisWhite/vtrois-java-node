package com.java.node.simple.eventHandle.handlerSerivce.impl;

import com.java.node.simple.eventHandle.dto.EventEnum;
import com.java.node.simple.eventHandle.dto.EventMsg;
import com.java.node.simple.eventHandle.dto.EventParam;
import com.java.node.simple.eventHandle.dto.HandleResult;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerCommonService;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerEnum;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerService;

/**
 *
 */
public class HandlerRecordActiveService extends HandlerCommonService implements HandlerService {

    @Override
    public HandlerEnum getHandleInfo() {
        return HandlerEnum.RECORD_ACTIVE;
    }

    @Override
    public boolean isMatch(EventParam dto) {
        EventMsg msg = dto.getMsg();
        return EventEnum.LOGIN.equals(msg.getEvent()) || EventEnum.ACTIVE.equals(msg.getEvent());
    }

    @Override
    public HandleResult proceed(EventParam dto) {
        return randomHandleResult();
    }
}
