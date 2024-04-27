package com.java.node.web.gracefulShutdown;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * @Description 这种执行方法，晚于 tomcat 的 Graceful shutdown
 */
@Slf4j
@Component
public class MyDisposableBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        log.info("检测到进程退出,执行自定义优雅停机,检测方式:{}", "DisposableBean");
//        GracefulShutdownTaskUtil.doShutdown();
        ThreadUtil.sleep(200);
        log.info("执行结束");
    }
}