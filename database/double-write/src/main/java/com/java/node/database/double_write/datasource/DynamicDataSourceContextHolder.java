package com.java.node.database.double_write.datasource;

import org.springframework.util.CollectionUtils;

import java.util.Stack;

public class DynamicDataSourceContextHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<Stack<String>> CONTEXT_HOLDER_STACK = new ThreadLocal<>();

    /**
     * 获得数据源的变量
     */
    public static String getDateSourceType() {
        if (CollectionUtils.isEmpty(CONTEXT_HOLDER_STACK.get())) {
            return null;
        }
        return CONTEXT_HOLDER_STACK.get().peek();
    }

    /**
     * 获取数据源类型
     */
    public static void setDateSourceType(String dateSourceType) {
        if (CONTEXT_HOLDER_STACK.get() == null) {
            CONTEXT_HOLDER_STACK.set(new Stack<>());
        }
        CONTEXT_HOLDER_STACK.get().push(dateSourceType);
    }

    /**
     * 清空所有的数据源变量
     */
    public static String clearDateSourceType() {
        return CONTEXT_HOLDER_STACK.get().pop();
    }

}
