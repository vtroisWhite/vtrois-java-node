package com.java.node.logback.log_config.config.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.fastjson.JSONObject;
import com.java.node.logback.log_config.config.LogConfigInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过给logback添加TurboFilter，直接过滤特定class打印的特定level的日志
 */
@Slf4j
public class LogLevelFilter implements LogConfigInterface {

    private static final String KEY = "logback.level.filter";
    private static final String logName = "过滤特定Level日志";
    private volatile static String prevFilterData;
    private volatile static Map<String, List<String>> filterLevelLogMap = new HashMap<>();

    @Override
    public String getName() {
        return logName;
    }

    @Override
    public synchronized void init(ApplicationContext context) {
        reload(context, null);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        for (TurboFilter turboFilter : loggerContext.getTurboFilterList()) {
            if (logName.equals(turboFilter.getName())) {
                log.warn("{}，turboFilter已存在", logName);
                return;
            }
        }
        TurboFilter turboFilter = new TurboFilter() {
            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
                List<String> list = filterLevelLogMap.get(logger.getName());
                if (list != null && list.contains(level.levelStr)) {
                    return FilterReply.DENY;
                }
                return FilterReply.NEUTRAL;
            }
        };
        turboFilter.setName(logName);
        logger.getLoggerContext().addTurboFilter(turboFilter);
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
        filterLevelLogMap = dataList.stream().collect(Collectors.toMap(FilterData::getClassName, FilterData::getFilterLevelList));
    }

    @Data
    private static class FilterData {
        private String className;
        private List<String> filterLevelList;
    }
}
