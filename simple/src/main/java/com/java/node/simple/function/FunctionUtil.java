package com.java.node.simple.function;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 *
 */
@Slf4j
public class FunctionUtil {
    public static void main(String[] args) {
        Param param = new Param();
        param.setName("tom");
        SFunction<Param, String> sf1 = Param::getName;
        String fieldName = getFieldName(sf1);
        Object oldValue = ReflectUtil.getFieldValue(param, fieldName);
        ReflectUtil.setFieldValue(param, fieldName, oldValue + "cat");
        System.out.println(StrUtil.format("字段名：{}，值：{}，设置后的值：{}", fieldName, oldValue, ReflectUtil.getFieldValue(param, fieldName)));
    }

    /**
     * 获取Function方法的字段名称
     *
     * @param fn
     * @param <T>
     * @return
     */
    public static <T> String getFieldName(SFunction<T, ?> fn) {
        try {
            // 从function取出序列化方法
            Method writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
            // 从序列化方法取出序列化的lambda信息
            boolean isAccessible = writeReplaceMethod.isAccessible();
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
            writeReplaceMethod.setAccessible(isAccessible);
            // 从lambda信息取出method、field、class等
            String fieldName = serializedLambda.getImplMethodName().substring("get".length());
            fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
            return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
        } catch (Exception e) {
            log.error("获取字段名称异常,e:", e);
            return null;
        }
    }

    /**
     * 让Function可以序列化
     *
     * @param <T>
     * @param <R>
     */
    @FunctionalInterface
    public interface SFunction<T, R> extends Function<T, R>, Serializable {
    }

    @Getter
    @Setter
    private static class Param {
        private String name;
    }
}
