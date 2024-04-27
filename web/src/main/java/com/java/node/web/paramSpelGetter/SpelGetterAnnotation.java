package com.java.node.web.paramSpelGetter;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface SpelGetterAnnotation {

    String[] countLimitExpress();

    String[] spelExpressions();

}
