package com.java.node.web.paramConverter.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @Description 用于将加了 {@link TimestampToDate} 注解的字段，将前端传来的时间戳转为Date数据
 * 参考：
 * 源码：{@link org.springframework.format.annotation.DateTimeFormat}
 * 源码：{@link org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory}
 * 博客：https://blog.csdn.net/chenhuaping007/article/details/80042165
 */
public class TimestampToDateFactory implements AnnotationFormatterFactory<TimestampToDate> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        Set<Class<?>> fieldTypes = new HashSet<Class<?>>();
        fieldTypes.add(Date.class);
        return fieldTypes;
    }

    @Override
    public Printer<?> getPrinter(TimestampToDate annotation, Class<?> fieldType) {
        return null;
    }

    @Override
    public Parser<?> getParser(TimestampToDate annotation, Class<?> fieldType) {
        return new TimestampToDateFormatter();
    }

    static class TimestampToDateFormatter implements Formatter<Date> {

        @Override
        public Date parse(String text, Locale locale) throws ParseException {
            return StrUtil.isBlank(text) ? null : new Date(Long.parseLong(text));
        }

        @Override
        public String print(Date object, Locale locale) {
            return null;
        }
    }
}