package com.java.node.simple.joinAndSplit;


import com.java.node.simple.joinAndSplit.custom.*;

import java.math.BigDecimal;

/**
 *
 */
public class JoinSplitTest {

    public static void main(String[] args) {

        ExtEntity entity = new ExtEntity();
        Custom1ExtDataSaveConfig.PHONE.setExtData(entity, "13312345678");
        //MTMzMTIzNDU2Nzg=
        System.out.println("在0号位存下手机号密文:" + entity.getExt1());
        Custom1ExtDataSaveConfig.MONEY.setExtData(entity, new BigDecimal("1.25"));
        //MTMzMTIzNDU2Nzg=,,,1.25
        System.out.println("在3号位存下金额:" + entity.getExt1());
        Custom1ExtDataSaveConfig.ADDRESS.setExtData(entity, "省,市1\\,区1\\,,县2\\\\,,,镇村街户");
        //MTMzMTIzNDU2Nzg=,,,1.25,省\,市1\\,区1\\,\,县2\\\,\,\,镇村街户
        System.out.println("在4号位存下地址:" + entity.getExt1());
        Custom1ExtDataSaveConfig.AGE.setExtData(entity, 17);
        //MTMzMTIzNDU2Nzg=,,17,1.25,省\,市1\\,区1\\,\,县2\\\,\,\,镇村街户
        System.out.println("在2号位存下年龄:" + entity.getExt1());

        //13312345678
        System.out.println("获取手机号:" + Custom1ExtDataSaveConfig.PHONE.getExtData(entity));
        //null
        System.out.println("获取姓名:" + Custom1ExtDataSaveConfig.NAME.getExtData(entity));
        //17
        System.out.println("获取年龄:" + Custom1ExtDataSaveConfig.AGE.getExtData(entity));
        //1.25
        System.out.println("获取金额:" + Custom1ExtDataSaveConfig.MONEY.getExtData(entity));
        //省,市1\,区1\,,县2\\,,,镇村街户
        System.out.println("获取地址:" + Custom1ExtDataSaveConfig.ADDRESS.getExtData(entity));

    }
}
