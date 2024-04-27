package com.java.node.simple.joinAndSplit.common;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public interface ExtDataHandleFunc<Entity, T> extends ExtDataSaveFunc<Entity, T> {
    /**
     * 描述
     */
    String getDesc();

    /**
     * 存储的下标
     */
    int getSaveIdx();

    /**
     * 获取ext扩展字段
     */
    Function<Entity, String> getGetExtFunc();

    /**
     * 保存ext扩展字段
     */
    BiConsumer<Entity, String> getSetExtFunc();

    /**
     * 将扩展数据格式化为字符串
     */
    Function<T, String> getFormatExtDataFunc();

    /**
     * 字符串转为扩展字段
     */
    Function<String, T> getParseExtDataFunc();

}
