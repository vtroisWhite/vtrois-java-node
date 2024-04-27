package com.java.node.web.gracefulShutdown;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description 晚于 tomcat优雅停机
 */
@Slf4j
@Component
public class CloseableImpl implements Closeable {

    @Override
    public void close() throws IOException {
        log.info("检测到进程退出,执行自定义优雅停机,检测方式:{}", "Closeable");
//        GracefulShutdownTaskUtil.doShutdown();
        ThreadUtil.sleep(200);
        log.info("执行结束");
    }
}
