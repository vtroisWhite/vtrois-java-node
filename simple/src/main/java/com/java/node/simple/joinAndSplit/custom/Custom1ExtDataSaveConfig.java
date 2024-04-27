package com.java.node.simple.joinAndSplit.custom;


import com.java.node.simple.joinAndSplit.common.*;

import java.math.BigDecimal;

/**
 *
 */
public interface Custom1ExtDataSaveConfig {

    ExtDataSaveFunc<ExtEntity, String> PHONE = ExtDataSaveConfig.buildEncrypt(
            "手机号",
            0,
            ExtEntity::getExt1,
            ExtEntity::setExt1
    );

    ExtDataSaveFunc<ExtEntity, String> NAME = ExtDataSaveConfig.buildString(
            "姓名",
            1,
            ExtEntity::getExt1,
            ExtEntity::setExt1
    );

    ExtDataSaveFunc<ExtEntity, Integer> AGE = ExtDataSaveConfig.buildInteger(
            "年龄",
            2,
            ExtEntity::getExt1,
            ExtEntity::setExt1
    );

    ExtDataSaveFunc<ExtEntity, BigDecimal> MONEY = ExtDataSaveConfig.buildDecimal(
            "余额",
            3,
            ExtEntity::getExt1,
            ExtEntity::setExt1
    );
    ExtDataSaveFunc<ExtEntity, String> ADDRESS = ExtDataSaveConfig.buildString(
            "地址",
            4,
            ExtEntity::getExt1,
            ExtEntity::setExt1
    );
}
