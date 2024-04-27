package com.java.node.simple.eventHandle.handlerSerivce.impl;

import com.java.node.simple.eventHandle.dto.*;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerCommonService;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerEnum;
import com.java.node.simple.eventHandle.handlerSerivce.HandlerService;

/**
 *
 */
public class HandlerSyncUserInfoService extends HandlerCommonService implements HandlerService {

    @Override
    public HandlerEnum getHandleInfo() {
        return HandlerEnum.SYNC_USER_INFO;
    }

    @Override
    public boolean isMatch(EventParam dto) {
        EventMsg msg = dto.getMsg();
        if (EventEnum.REGISTER.equals(msg.getEvent())) {
            return true;
        }
        if (EventEnum.VERIFY_ID_CARD.equals(msg.getEvent()) && SubEventEnum.FRONT.equals(msg.getSubEvent())) {
            return true;
        }
        return false;
    }

    @Override
    public HandleResult proceed(EventParam dto) {
        return randomHandleResult();
    }
}
