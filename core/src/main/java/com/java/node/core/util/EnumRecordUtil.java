package com.java.node.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于记录任务执行结果的工具类
 */
public class EnumRecordUtil {

    /**
     * 使用公共的枚举记录，防止每个任务都需要创建个枚举
     *
     * @return
     */
    public static Map<RecordEnum, AtomicInteger> initWithCommonRecordEnum(RecordEnum... recordEnums) {
        Map<RecordEnum, AtomicInteger> res = new LinkedHashMap<>();
        for (RecordEnum value : recordEnums.length == 0 ? RecordEnum.values() : recordEnums) {
            res.put(value, new AtomicInteger(0));
        }
        return res;
    }

    /**
     * 初始化记录结果对象
     *
     * @return
     */
    public static <T extends Enum> Map<T, AtomicInteger> initExecuteResultRecordMap(Collection<T> enumList) {
        Map<T, AtomicInteger> res = new LinkedHashMap<>();
        for (Enum value : enumList) {
            res.put((T) value, new AtomicInteger(0));
        }
        return res;
    }

    /**
     * 初始化记录结果对象
     *
     * @return
     */
    public static <T extends Enum> Map<T, AtomicInteger> initExecuteResultRecordMap(Class<T> t) {
        Map<T, AtomicInteger> res = new LinkedHashMap<>();
        for (Enum value : t.getEnumConstants()) {
            res.put((T) value, new AtomicInteger(0));
        }
        return res;
    }

    /**
     * 生成执行结果快照
     *
     * @param map
     * @return
     */
    public static <T extends Enum> Map<T, Integer> copyRecordSnapShoot(Map<T, AtomicInteger> map) {
        Map<T, Integer> res = new HashMap<>();
        map.forEach((k, v) -> res.put(k, v.get()));
        return res;
    }

    /**
     * 格式化执行结果
     */
    public static <T extends Enum> String formatExecuteResult(Map<T, AtomicInteger> resultAtomicIntegerMap, Map<T, Integer> prevResultSnapShoot) {
        StringBuffer sb = new StringBuffer();
        resultAtomicIntegerMap.forEach((k, v) -> {
            Integer executeNumber = v.get();
            Integer prevNumber = prevResultSnapShoot == null ? 0 : prevResultSnapShoot.get(k);
            sb.append(String.format("%s:%s条,", k.toString(), executeNumber - prevNumber));
        });
        return sb.toString();
    }

    /**
     * 公共的枚举类
     */
    public enum RecordEnum {
        /**
         * 总数
         */
        TOTAL,
        /**
         * 成功
         */
        SUCCESS,
        /**
         * 失败
         */
        ERROR,
        /**
         * 跳过
         */
        SKIP,
        /**
         * 缺少参数、数据
         */
        MISS_DATA,
        ;
    }
}
