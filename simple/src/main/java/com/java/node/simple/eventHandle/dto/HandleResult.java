package com.java.node.simple.eventHandle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import static com.java.node.simple.eventHandle.dto.HandleResult.ResultEnum.*;


/**
 * 处理结果
 */
@Data
@AllArgsConstructor
public class HandleResult {

    private final ResultEnum resultEnum;
    private final String msg;

    public static HandleResult success() {
        return new HandleResult(SUCCESS, null);
    }

    public static HandleResult error(String msg) {
        return new HandleResult(ERROR, msg);
    }

    /**
     * 缺少参数
     *
     * @param msg
     * @return
     */
    public static HandleResult missData(String msg) {
        return new HandleResult(MISS_DATA, msg);
    }

    /**
     * 不需要的数据，无需处理
     *
     * @param msg
     * @return
     */
    public static HandleResult notNeed(String msg) {
        return new HandleResult(NOT_NEED, msg);
    }

    public boolean isSuccess() {
        return SUCCESS == this.resultEnum;
    }

    public boolean isError() {
        return ERROR == this.resultEnum;
    }

    public boolean isMissData() {
        return MISS_DATA == this.resultEnum;
    }

    public boolean isNotNeed() {
        return NOT_NEED == this.resultEnum;
    }

    @AllArgsConstructor
    public enum ResultEnum {
        SUCCESS(0, "成功"),
        MISS_DATA(1, "缺少数据"),
        NOT_NEED(2, "无需处理"),
        ERROR(999, "失败"),
        ;
        @Getter
        private final int code;
        @Getter
        private final String desc;

    }
}
