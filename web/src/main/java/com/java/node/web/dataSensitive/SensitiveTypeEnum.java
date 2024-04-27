package com.java.node.web.dataSensitive;

import cn.hutool.core.util.DesensitizedUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * 脱敏类型
 */
@AllArgsConstructor
public enum SensitiveTypeEnum {
    /**
     * 姓名
     */
    NAME(DesensitizedUtil::chineseName),
    /**
     * 身份证
     */
    ID_CARD(x -> DesensitizedUtil.idCardNum(x, 4, 4)),
    /**
     * 手机号码
     */
    PHONE(DesensitizedUtil::mobilePhone),
    /**
     * 银行卡号
     */
    BANK_CARD(DesensitizedUtil::bankCard),
    ;

    /**
     * 脱敏方法
     */
    @Getter
    private final Function<String, String> maskFunction;
}
