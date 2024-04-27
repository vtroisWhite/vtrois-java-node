package com.java.node.web.requestHandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在AOP之前接收并数据传参
 * 反参后在AOP之后进行反参的处理
 */

@Slf4j
@RestController
public class RequestHandleController {

    @GetMapping("/request/handle/get")
    public RequestHandleResponse getTest(RequestHandleParam param) {
        log.info("入参：" + param);
        return new RequestHandleResponse("get处理成功", null, null);
    }

    @PostMapping("/request/handle/post-form")
    public RequestHandleResponse postForm(RequestHandleParam param) {
        log.info("入参：" + param);
        return new RequestHandleResponse("postForm处理成功", null, null);
    }

    @PostMapping("/request/handle/post-json")
    public RequestHandleResponse postJSON(@RequestBody RequestHandleParam param) {
        log.info("入参：" + param);
        return new RequestHandleResponse("postJSON处理成功", null, null);
    }
}
