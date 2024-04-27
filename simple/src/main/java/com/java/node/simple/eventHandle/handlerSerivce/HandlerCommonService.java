package com.java.node.simple.eventHandle.handlerSerivce;

import cn.hutool.core.util.RandomUtil;
import com.java.node.simple.eventHandle.dto.HandleResult;

/**
 * 公共方法
 */
public class HandlerCommonService {


    public boolean randomBoolean() {
        return RandomUtil.randomBoolean();
    }


    public HandleResult randomHandleResult() {
        int i = RandomUtil.randomInt(2);
        switch (i) {
            case 0:
                return HandleResult.success();
            case 1:
                return HandleResult.missData("缺少a");
            case 2:
                return HandleResult.notNeed("数据不符合");
            default:
                return null;
        }
    }
}
