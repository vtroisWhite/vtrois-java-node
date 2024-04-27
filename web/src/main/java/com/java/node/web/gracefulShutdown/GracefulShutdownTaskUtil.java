package com.java.node.web.gracefulShutdown;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description
 */
@Slf4j
public class GracefulShutdownTaskUtil {

    private static final Map<String, GracefulShutdownTaskParam> taskMap = Collections.synchronizedMap(new LinkedHashMap<>());

    public static void add(GracefulShutdownTaskParam param) {
        String taskName = param.getTaskName();
        if (taskMap.containsKey(taskName)) {
            log.error("优雅停机,已存在的taskName:{}", taskName);
        }
        taskMap.put(param.getTaskName(), param);
        log.info("优雅停机,添加新任务成功:{}", taskName);
    }

    public static void removeTask(String taskName) {
        GracefulShutdownTaskParam v = taskMap.remove(taskName);
        log.info("优雅停机,删除任务{}:{}", v == null ? "失败" : "成功", taskName);
    }

    public synchronized static void doShutdown() {
        log.info("开始执行自定义优雅停机");
        List<List<GracefulShutdownTaskParam>> taskGroupList = splitTaskGroup();
        for (List<GracefulShutdownTaskParam> taskList : taskGroupList) {
            for (int i = 0; i < taskList.size(); i++) {
                GracefulShutdownTaskParam task = taskList.get(i);
                if (!task.isAsyncClose()) {
                    doShutdownTask(task);
                    continue;
                }
                List<GracefulShutdownTaskParam> asyncList = taskList.subList(i, taskList.size());
                CompletableFuture.allOf(asyncList.stream()
                        .map(param -> CompletableFuture.runAsync(() -> doShutdownTask(param)))
                        .toArray(CompletableFuture[]::new)
                ).join();
                break;
            }
        }
    }

    public static List<List<GracefulShutdownTaskParam>> splitTaskGroup() {
        List<GracefulShutdownTaskParam> collect = taskMap.values().stream().sorted((a, b) -> {
            if (a.getOrder() != b.getOrder()) {
                return Integer.compare(a.getOrder(), b.getOrder());
            }
            if (a.isAsyncClose() != b.isAsyncClose()) {
                return a.isAsyncClose() ? 1 : -1;
            }
            return Integer.compare(a.getId(), b.getId());
        }).collect(Collectors.toList());

        List<List<GracefulShutdownTaskParam>> taskGroupList = new ArrayList<>();
        List<GracefulShutdownTaskParam> group = new ArrayList<>();
        for (GracefulShutdownTaskParam param : collect) {
            if (group.isEmpty()) {
                group.add(param);
                taskGroupList.add(group);
                continue;
            }
            if (group.get(0).getOrder() == param.getOrder()) {
                group.add(param);
                continue;
            }
            group = new ArrayList<>();
            group.add(param);
            taskGroupList.add(group);
        }
        for (int i = 0; i < taskGroupList.size(); i++) {
            List<String> list2 = taskGroupList.get(i).stream().map(GracefulShutdownTaskParam::getTaskName).collect(Collectors.toList());
            log.info("优雅停机,第{}批执行任务:{}", i + 1, JSON.toJSONString(list2));
        }
        return taskGroupList;
    }

    private static void doShutdownTask(GracefulShutdownTaskParam param) {
        try {
            log.info("优雅停机,开始执行任务:{}", param.getTaskName());
            param.getTaskFunction().accept(param.getObj());
            log.info("优雅停机,任务执行完成:{}", param.getTaskName());
            removeTask(param.getTaskName());
        } catch (Exception e) {
            log.error("优雅停机,任务执行异常,taskName:{},e:", param.getTaskName(), e);
        }
    }
}
