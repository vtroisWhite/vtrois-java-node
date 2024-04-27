package com.java.node.web.paramConverter.config;

import java.lang.annotation.*;

/**
 * @Description 将form表单的对象转为对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE,})
@Documented
public @interface ObjectFormat {

    /**
     * 数据类型
     */
    ObjectType objectType() default ObjectType.OBJECT;

    enum ObjectType {
        OBJECT,
        ARRAY,
    }
}
