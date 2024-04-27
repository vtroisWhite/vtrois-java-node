package com.java.node.simple.timeRecord;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description 可记录每个节点的耗时情况
 */
@Data
public class TimeRecordTest {

    public static void main(String[] args) throws Exception {
        TimeRecord record = TimeRecord.initRecord();
        Runnable runnable = () -> doTask(400);

        Supplier<Integer> supplier = () -> doTask(400);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        record.recordCallable("callable1", supplier).call();
        executorService.submit(record.recordCallable("callable2", supplier)).get();
        record.record("supplier1", supplier);
        record.record("runnable1", runnable);

        record.setCostTs(record.interval());
        System.out.println(JSON.toJSONString(record));
    }


    @SneakyThrows
    public static int doTask(int sleep) {
        Thread.sleep(sleep);
        return 1;
    }
}
