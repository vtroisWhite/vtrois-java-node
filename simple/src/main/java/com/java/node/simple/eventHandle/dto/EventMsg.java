package com.java.node.simple.eventHandle.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

/**
 * 外部系统发来的数据包
 */
@Data
@Accessors(chain = true)
public class EventMsg {

    private String uuid;
    /**
     * 版本号，通过生产者消息中的版本号，来确认其版本信息，进而判断是否要执行新策略
     */
    private int ver;
    /**
     * 事件，必传
     */
    private EventEnum event;
    /**
     * 二级事件
     */
    private SubEventEnum subEvent;
    /**
     * 事件种类
     */
    private String eventType;
    /**
     * 数据信息
     */
    private JSONObject data;
    /**
     * 指定处理器
     */
    private List<String> handlerList;
    /**
     * 事件时间
     * 必传
     */
    private Long eventTime;

    public static EventMsg buildWithDefValue() {
        EventMsg msg = new EventMsg();
        msg.setUuid(UUID.randomUUID().toString());
        msg.setVer(3);
        msg.setEventTime(System.currentTimeMillis());
        return msg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
