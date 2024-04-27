package com.java.node.simple.eventHandle.handlerSerivce;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理器事件
 */
@AllArgsConstructor
public enum HandlerEnum {
    /**
     * 发送营销短信：注册、绑卡
     */
    MARKETING("发送营销短信", 1),
    /**
     * 保存 登录、活跃 数据
     */
    RECORD_ACTIVE("记录活跃记录", 1),
    /**
     * 注册、实名认证  同步用户信息
     */
    SYNC_USER_INFO("同步用户信息", 1),

    ;

    @Getter
    private final String desc;
    /**
     * 需要的数据版本号
     */
    @Getter
    private final int requireVersion;

}
