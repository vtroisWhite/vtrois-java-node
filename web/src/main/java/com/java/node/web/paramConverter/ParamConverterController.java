package com.java.node.web.paramConverter;

import com.alibaba.fastjson.JSON;
import com.java.node.web.paramConverter.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 格式化前端传来的参数，解决直接接受页面复杂传参报错的问题
 */

@Slf4j
@RestController
public class ParamConverterController {

    /**
     * 没有 TimestampToDate 注解Date参数，前端直接传来时间戳，报错
     */
    @PostMapping("/param/converter/timestamp/1")
    public void test_TimestampToDate_1(TimestampToDateParam1 param) {
        log.info(JSON.toJSONString(param));
    }


    /**
     * 有 TimestampToDate 注解Date参数，前端直接传来时间戳，通过
     */
    @PostMapping("/param/converter/timestamp/2")
    public void test_TimestampToDate_2(TimestampToDateParam2 param) {
        log.info(JSON.toJSONString(param));
    }


    /**
     * WebMvcConfigurer中没有配置 List<InnerParam> 的转换规则，报错
     */
    @PostMapping("/param/converter/1")
    public void test_Converter_1(ConverterTestParam1 param) {
        log.info(JSON.toJSONString(param));
    }


    /**
     * WebMvcConfigurer中 配置 List<InnerParam> 的转换规则，通过
     * {@link com.java.node.web.paramConverter.config.MyWebAppConfigurer}
     */
    @PostMapping("/param/converter/2")
    public void test_Converter_2(ConverterTestParam2 param) {
        log.info(JSON.toJSONString(param));
    }


    /**
     * 使用 @ObjectFormat 注解测试转换Object
     */
    @PostMapping("/param/converter/objectFormat")
    public void test_Converter_objectFormat(ObejctFormatTestParam param) {
        log.info(JSON.toJSONString(param));
    }

    /**
     * 使用 @ObjectFormat 注解测试转换List泛型
     */
    @PostMapping("/param/converter/collectionFormat")
    public void test_Converter_CollectionFormat(CollectionTestParam param) {
        log.info(JSON.toJSONString(param));
        log.info(JSON.toJSONString(param.getList()));
        log.info(JSON.toJSONString(param.getList().get(0)));
        log.info(JSON.toJSONString(param.getList().get(0).getCfgList()));
    }

}
