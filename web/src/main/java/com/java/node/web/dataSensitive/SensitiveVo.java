package com.java.node.web.dataSensitive;

import lombok.Data;

/**
 * @Description
 */
@Data
public class SensitiveVo {
    private Integer uid;
    /**
     * 姓名
     */
    @Sensitive(SensitiveTypeEnum.NAME)
    private String userName;
    /**
     * 注册手机号
     */
    @Sensitive(SensitiveTypeEnum.PHONE)
    private String registerPhone;
    /**
     * 银行卡号
     */
    @Sensitive(SensitiveTypeEnum.BANK_CARD)
    private String bankCardNum;
    /**
     * 预留手机号
     */
    @Sensitive(SensitiveTypeEnum.ID_CARD)
    private String cid;
}
