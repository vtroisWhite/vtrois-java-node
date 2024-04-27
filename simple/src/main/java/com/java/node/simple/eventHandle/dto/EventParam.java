package com.java.node.simple.eventHandle.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 内部数据流通数据包，包装一层，方便数据扩展
 */
@Data
@Accessors(chain = true)
public class EventParam {

    private EventMsg msg;
}
