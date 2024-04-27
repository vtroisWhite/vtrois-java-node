package com.java.node.web.paramConverter.config;

import java.lang.annotation.*;

/**
 * @Description 将前端传来的时间戳转为date
 * <p>
 * 参考：
 * 源码：{@link org.springframework.format.annotation.DateTimeFormat}
 * 源码：{@link org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory}
 * 博客：https://blog.csdn.net/chenhuaping007/article/details/80042165
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface TimestampToDate {
}
