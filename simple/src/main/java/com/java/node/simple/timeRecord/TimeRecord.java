package com.java.node.simple.timeRecord;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description 可记录每个节点的耗时情况
 */
@Data
public class TimeRecord {
    /**
     * 开始时的时间戳
     */
    private Long startTs;
    /**
     * 实际耗时多少毫秒
     */
    private Long costTs;
    /**
     * 各个节点耗时详情
     */
    private Map<String, TimeRecordDTO> nodeTimeMap;

    public static TimeRecord initRecord() {
        TimeRecord record = new TimeRecord();
        record.setStartTs(System.currentTimeMillis());
        record.setNodeTimeMap(new ConcurrentHashMap<>());
        return record;
    }

    public <T> Callable<T> recordCallable(String node, Supplier<T> supplier) {
        return recordCallable(TimeRecord::getNodeTimeMap, node, supplier);
    }

    public <T> Callable<T> recordCallable(Function<TimeRecord, Map<String, TimeRecordDTO>> mapFunc, String node, Supplier<T> supplier) {
        this.recordStart(mapFunc, node);
        return () -> {
            try {
                return supplier.get();
            } finally {
                recordEnd(mapFunc, node);
            }
        };
    }

    public <T> T record(String node, Supplier<T> supplier) {
        return record(TimeRecord::getNodeTimeMap, node, supplier);
    }

    public <T> T record(Function<TimeRecord, Map<String, TimeRecordDTO>> mapFunc, String node, Supplier<T> supplier) {
        try {
            this.recordStart(mapFunc, node);
            return supplier.get();
        } finally {
            recordEnd(mapFunc, node);
        }
    }

    public void record(String node, Runnable runnable) {
        record(TimeRecord::getNodeTimeMap, node, runnable);
    }

    public void record(Function<TimeRecord, Map<String, TimeRecordDTO>> mapFunc, String node, Runnable runnable) {
        record(mapFunc, node, () -> {
            runnable.run();
            return null;
        });
    }

    public void recordStart(Function<TimeRecord, Map<String, TimeRecordDTO>> mapFunc, String node) {
        mapFunc.apply(this).putIfAbsent(node, new TimeRecordDTO().setStartTime(interval()));
    }

    public void recordEnd(Function<TimeRecord, Map<String, TimeRecordDTO>> mapFunc, String node) {
        TimeRecordDTO dto = mapFunc.apply(this).get(node);
        if (dto.getEndTime() == null) {
            dto.setEndTime(interval());
            dto.setCost(dto.getEndTime() - dto.getStartTime());
        }
    }

    public Long interval() {
        return System.currentTimeMillis() - getStartTs();
    }

    /**
     * @Description 时间节点记录
     */
    @Data
    @Accessors(chain = true)
    public static class TimeRecordDTO {
        /**
         * 起始时间，（相对值，以进入接口开始执行为0算起）
         */
        private Long startTime;
        /**
         * 结束时间
         */
        private Long endTime;
        /**
         * 使用时间
         */
        private Long cost;
    }
}
