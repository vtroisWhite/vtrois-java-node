package com.java.node.simple.eventHandle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.java.node.core.util.EnumRecordUtil;
import com.java.node.simple.eventHandle.dto.*;
import com.java.node.simple.eventHandle.handlerSerivce.*;
import com.java.node.simple.eventHandle.handlerSerivce.impl.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 主方法
 */
@Slf4j
public class EventHandleTest {

    private static final List<HandlerService> handleServiceList = Arrays.asList(new HandlerRecordActiveService(), new HandlerMarkingService(), new HandlerSyncUserInfoService());
    /**
     * 记录每个消息接收了多少
     */
    private static final Map<EventEnum, AtomicInteger> counter = EnumRecordUtil.initExecuteResultRecordMap(EventEnum.class);
    private static final Map<EventEnum, Map<SubEventEnum, AtomicInteger>> subCounter = new HashMap<EventEnum, Map<SubEventEnum, AtomicInteger>>() {{
        for (EventEnum eventEnum : EventEnum.values()) {
            put(eventEnum, EnumRecordUtil.initExecuteResultRecordMap(SubEventEnum.getByParentEvent(eventEnum)));
        }
    }};
    private static final List<HandleResult.ResultEnum> resultEnumList = ListUtil.toList(HandleResult.ResultEnum.values());

    public static void main(String[] args) {
        long currentTimeMillis = System.currentTimeMillis();
        EventHandleTest eventHandleTest = new EventHandleTest();
        EventEnum[] eventEnums = EventEnum.values();
        List<List<SubEventEnum>> subEventEnums = Arrays.stream(eventEnums).map(SubEventEnum::getByParentEvent).collect(Collectors.toList());
        for (int i = 0; i < 10000; i++) {
            EventMsg eventMsg = EventMsg.buildWithDefValue();
            int eventIdx = RandomUtil.randomInt(eventEnums.length);
            eventMsg.setEvent(eventEnums[eventIdx]);
            List<SubEventEnum> subEvent = subEventEnums.get(eventIdx);
            eventMsg.setSubEvent(subEvent.get(RandomUtil.randomInt(subEvent.size())));
            eventHandleTest.counter(eventMsg);
            eventHandleTest.handle(eventMsg);
        }
        eventHandleTest.printCounter(currentTimeMillis, 0, new InheritableThreadLocal<>());
    }

    private void loadCounter() {
        final long start = System.currentTimeMillis();
        //多少分钟打印一次
        final int period = 30;
        InheritableThreadLocal<Map<EventEnum, Integer>> counterSnapShoot = new InheritableThreadLocal<>();
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            printCounter(start, period, counterSnapShoot);
        }, period, period, TimeUnit.MINUTES);
        log.info("事件处理,打印计数器,加载完成");
    }

    /**
     * 打印执行统计
     */
    private void printCounter(long start, int period, InheritableThreadLocal<Map<EventEnum, Integer>> counterSnapShoot) {
        try {
            String snapShootRes = EnumRecordUtil.formatExecuteResult(counter, counterSnapShoot.get());
            counterSnapShoot.set(EnumRecordUtil.copyRecordSnapShoot(counter));
            String totalRes = EnumRecordUtil.formatExecuteResult(counter, null);
            log.warn("事件处理,已运行{},处理情况:【{}】,在过去的{}分钟处理情况【{}】,子事件总处理结果：【{}】",
                    DateUtil.formatBetween(System.currentTimeMillis() - start), totalRes, period, snapShootRes, JSON.toJSONString(subCounter));
        } catch (Exception e) {
            log.error("printCounter exception.e", e);
        }
    }

    /**
     * 计数
     */
    private void counter(EventMsg msg) {
        counter.get(msg.getEvent()).incrementAndGet();
        if (msg.getSubEvent() != null) {
            subCounter.get(msg.getEvent()).get(msg.getSubEvent()).incrementAndGet();
        }
    }

    /**
     * 处理任务
     *
     * @param msg
     */
    public void handle(EventMsg msg) {
        TimeInterval timer = DateUtil.timer();
        //构建对象
        EventParam dto = new EventParam().setMsg(msg);
        //处理数据
        List<List<String>> resultRecord = resultEnumList.stream().map(x -> new ArrayList<String>()).collect(Collectors.toList());
        for (HandlerService handleService : handleServiceList) {
            HandlerEnum handleInfo = handleService.getHandleInfo();
            HandleResult result = null;
            try {
                if (!CollUtil.isEmpty(msg.getHandlerList()) && !msg.getHandlerList().contains(handleInfo.name())) {
                    //如果指定了处理器，但又不是这个处理器，不处理
                    continue;
                }
                if (handleInfo.getRequireVersion() > msg.getVer()) {
                    //版本号过低,不处理
                    continue;
                }
                if (!handleService.isMatch(dto)) {
                    //不属于这个处理器
                    continue;
                }
                //处理数据
                result = handleService.proceed(dto);
                if (!result.isSuccess()) {
                    log.info("埋点处理,未成功,{},处理器:{},msg:{}", result.getResultEnum().getDesc(), handleInfo.getDesc(), result.getMsg());
                }
            } catch (Exception e) {
                result = HandleResult.error("服务异常");
                log.error("埋点处理,处理异常,处理器:{},e:", handleInfo.getDesc(), e);
            } finally {
                if (result != null && result.getResultEnum() != null) {
                    resultRecord.get(resultEnumList.indexOf(result.getResultEnum())).add(handleInfo.getDesc());
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resultRecord.size(); i++) {
            sb.append(String.format("%s:%s,", resultEnumList.get(i).getDesc(), resultRecord.get(i)));
        }
        log.info("埋点处理,{}耗时:{}ms", sb, timer.interval());
    }
}
