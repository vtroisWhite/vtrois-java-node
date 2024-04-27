package com.java.node.web.paramConverter.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 */
public class ObejctFormatFactory implements AnnotationFormatterFactory<ObjectFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        Set<Class<?>> fieldTypes = new HashSet<Class<?>>();
        fieldTypes.add(Object.class);
        return fieldTypes;
    }

    @Override
    public Printer<?> getPrinter(ObjectFormat annotation, Class<?> fieldType) {
        return null;
    }

    @Override
    public Parser<?> getParser(ObjectFormat annotation, Class<?> fieldType) {
        return new ObjectFormatter(annotation, fieldType);
    }

    static class ObjectFormatter implements Formatter<Object> {

        private final ObjectFormat annotation;

        /**
         * 注解字段的类，如果标注在List<T>上，则这里是T的类型
         */
        private final Class<?> fieldType;

        public ObjectFormatter(ObjectFormat annotation, Class<?> fieldType) {
            this.annotation = annotation;
            this.fieldType = fieldType;
        }

        @Override
        public Object parse(String text, Locale locale) {
            if (StrUtil.isBlank(text)) {
                return null;
            }
            if (annotation.objectType() == ObjectFormat.ObjectType.ARRAY) {
                //集合类
                return JSONObject.parseArray(text, fieldType);
            }
            return JSONObject.parseObject(text, fieldType);
        }

        @Override
        public String print(Object object, Locale locale) {
            return null;
        }
    }
}