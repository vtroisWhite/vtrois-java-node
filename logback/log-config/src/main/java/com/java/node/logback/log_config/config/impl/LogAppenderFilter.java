package com.java.node.logback.log_config.config.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.fastjson.JSONObject;
import com.java.node.logback.log_config.config.LogConfigInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过给logback的appender添加Filter，实现特定class不往特定appender打印日志，从而做到error日志依旧在向info文件打印，但是不打印到error文件，从而不触发报警
 */
@Slf4j
public class LogAppenderFilter implements LogConfigInterface {

    private static final String KEY = "logback.appender.filter";
    private static final String logName = "日志过滤appender";
    private volatile static String prevFilterData;
    private volatile static Map<String, List<String>> filterLevelLogMap = new HashMap<>();

    @Override
    public String getName() {
        return logName;
    }

    /**
     * 大致打印日志流程：
     * 获取所有的appender，如 控制台、INFO、WARN、ERROR，进行打印日志操作，代码：
     * {@link ch.qos.logback.classic.Logger#callAppenders(ILoggingEvent)}
     * 再遍历appender的所有Filter，看是否可以在此appender打印，代码：
     * {@link ch.qos.logback.core.spi.FilterAttachableImpl#getFilterChainDecision}
     */
    @Override
    public synchronized void init(ApplicationContext context) {
        reload(context, null);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        Iterator<Appender<ILoggingEvent>> appenderIterator = logger.iteratorForAppenders();
        while (appenderIterator.hasNext()) {
            Appender<ILoggingEvent> appender = appenderIterator.next();
            String name = appender.getName().toUpperCase();
            log.warn("{},为appender增加过滤器：{}", logName, name);
            Filter<ILoggingEvent> filter = new Filter<>() {
                @Override
                public FilterReply decide(ILoggingEvent event) {
                    List<String> list = filterLevelLogMap.get(event.getLoggerName());
                    if (list != null && list.contains(name)) {
                        return FilterReply.DENY;
                    }
                    return FilterReply.NEUTRAL;
                }
            };
            filter.start();
            appender.addFilter(filter);
        }
    }

    @Override
    public synchronized void reload(ApplicationContext context, Set<String> keys) {
        if (keys != null && !keys.contains(KEY)) {
            return;
        }
        String property = context.getEnvironment().getProperty(KEY);
        if (Objects.equals(prevFilterData, property)) {
            return;
        }
        log.warn("{}，由{}，变为{}", logName, prevFilterData, property);
        prevFilterData = property;
        List<FilterData> dataList = JSONObject.parseArray(property, FilterData.class);
        if (CollectionUtils.isEmpty(dataList)) {
            filterLevelLogMap = Collections.emptyMap();
            return;
        }
        filterLevelLogMap = dataList.stream().collect(Collectors.toMap(FilterData::getClassName, FilterData::getFilterAppenderList));
    }

    @Data
    private static class FilterData {
        private String className;
        private List<String> filterAppenderList;
    }
}
