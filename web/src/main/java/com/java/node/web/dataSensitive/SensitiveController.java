package com.java.node.web.dataSensitive;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 */
@Slf4j
@RestController
public class SensitiveController {

    @GetMapping("/sensitive/getUser")
    public SensitiveVo getUser() {
        SensitiveVo vo = new SensitiveVo();
        vo.setUid(123);
        vo.setUserName("云天明");
        vo.setRegisterPhone("13312345678");
        vo.setBankCardNum("6212260001007512123");
        vo.setCid("14013320000101002X");
        log.info("返回数据：\n{}", JSON.toJSONString(vo));
        return vo;
    }
}
