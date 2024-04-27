package com.java.node.web.paramConverter.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.core.convert.converter.Converter;
import com.java.node.web.paramConverter.param.ConverterTestParam2;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        //传参带了TimestampToDate 注解的字段，前端传来的时间戳转为Date
        registry.addFormatterForFieldAnnotation(new TimestampToDateFactory());
        //使用注解，格式化form表单的复杂对象
        registry.addFormatterForFieldAnnotation(new ObejctFormatFactory());

        //form表单传的json格式字符串，转为对象接受
        registry.addConverter(new Converter<String, List<ConverterTestParam2.InnerParam>>() {
            @Override
            public List<ConverterTestParam2.InnerParam> convert(String source) {
                if (!StrUtil.isEmpty(source)) {
                    return JSON.parseArray(source, ConverterTestParam2.InnerParam.class);
                }
                return null;
            }
        });
    }

}