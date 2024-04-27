package com.java.node.logback.log_config.config.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;
import com.java.node.logback.log_config.config.LogConfigInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通过变更Logger的level（日志输出等级），从而实现特定class的特等level的实现
 * 比如某些class改为OFF不输出日志，某些class改为debug输出debug日志
 */
@Slf4j
public class LogLevelChange implements LogConfigInterface {

    /**
     * 默认的level的备份，当配置的特定level删除时，要回退回之前的默认level
     */
    private final static Map<String, Level> defaultLevel = new HashMap<>();

    private static final String KEY = "logback.level.change";
    private static final String logName = "修改日志输出等级";
    private volatile static String prevChangeData;
    private volatile static Map<String, DynamicData> prevChangeMap = new HashMap<>();

    @Override
    public String getName() {
        return logName;
    }

    @Override
    public void init(ApplicationContext context) {
        reload(context, null);
    }

    @Override
    public void reload(ApplicationContext context, Set<String> keys) {
        String property = context.getEnvironment().getProperty(KEY);
        if (Objects.equals(prevChangeData, property)) {
            return;
        }
        List<DynamicData> dynamicDataList = JSONObject.parseArray(property, DynamicData.class);
        if (dynamicDataList == null) {
            dynamicDataList = new ArrayList<>();
        }
        Map<String, DynamicData> collectMap = dynamicDataList.stream().collect(Collectors.toMap(DynamicData::getClassName, Function.identity()));
        for (DynamicData data : dynamicDataList) {
            Logger logger = (Logger) LoggerFactory.getLogger(data.getClassName());
            Level level = Level.toLevel(data.getLevel(), null);
            if (logger == null || level == null) {
                log.error("{},DynamicData数据异常：{}", logName, data);
                return;
            }
            Level effectiveLevel = logger.getEffectiveLevel();
            defaultLevel.putIfAbsent(data.getClassName(), effectiveLevel);
            if (effectiveLevel.levelInt == level.levelInt) {
                continue;
            }
            logger.setLevel(level);
            log.warn("{}，等级变更，class：{}，由{}变为{}", logName, data.getClassName(), effectiveLevel.levelStr, level.levelStr);
        }
        for (DynamicData data : prevChangeMap.values()) {
            if (!collectMap.containsKey(data.getClassName())) {
                Logger logger = (Logger) LoggerFactory.getLogger(data.getClassName());
                Level level = defaultLevel.get(data.getClassName());
                log.warn("{}，回退默认，class：{}，由{}变为{}", logName, data.getClassName(), logger.getEffectiveLevel().levelStr, level.levelStr);
                logger.setLevel(level);
            }
        }
        log.warn("{}，全部完成，由{}，变为{}", logName, prevChangeData, property);
        prevChangeMap = collectMap;
        prevChangeData = property;
    }


    @Data
    private static class DynamicData {
        private String className;
        private String level;
    }

}
