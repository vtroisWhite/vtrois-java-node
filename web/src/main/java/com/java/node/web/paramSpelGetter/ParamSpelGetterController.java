package com.java.node.web.paramSpelGetter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 根据spel表达式获取方法入参
 */
@Slf4j
@RestController
public class ParamSpelGetterController {

    @SpelGetterAnnotation(
            countLimitExpress = {
                    "${server.port:1234}",
                    "${server.ppp:1234}",
            },
            spelExpressions = {
                    "#key",
                    "#param",
                    "#param.id",
                    "#param.next.name",
                    "#param.nextList[1].name",
                    "'nextList中第一个数组的id为:'+(#param.nextList[0].id)",
            })
    @PostMapping("/param/spel/getter/1")
    public void test1(@RequestParam String key, @RequestBody ParamSpelGetterParam param) {
        log.info("入参,key:{},param:{}", key, JSON.toJSONString(param));
    }
}
