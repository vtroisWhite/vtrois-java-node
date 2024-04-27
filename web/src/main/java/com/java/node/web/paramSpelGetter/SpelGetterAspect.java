package com.java.node.web.paramSpelGetter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Order(-1)
@Component
@Slf4j
public class SpelGetterAspect {

    private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();
    private final ExpressionParser parser = new SpelExpressionParser();
    @Autowired
    private BeanFactory beanFactory;

    @Before(value = "@annotation(annotation)")
    public void changeDataSource(JoinPoint point, SpelGetterAnnotation annotation) {
        try {
            for (String express : annotation.countLimitExpress()) {
                String value = parser.parseExpression(resolve(express), PARSER_CONTEXT).getValue(String.class);
                log.info("countLimitExpress,key:{},value:{}", express, value);
            }

            Map<String, Object> reqParamMap = bindParam(getMethod(point), point.getArgs());
            EvaluationContext context = new StandardEvaluationContext();
            reqParamMap.keySet().forEach(k -> context.setVariable(k, reqParamMap.get(k)));
            ExpressionParser parser = new SpelExpressionParser();
            log.info("切面：{}", JSON.toJSONString(bindParam(getMethod(point), point.getArgs())));
            for (String expression : annotation.spelExpressions()) {
                log.info("spel表达式解析:{}", parser.parseExpression(expression).getValue(context));
            }
        } catch (Exception e) {
            log.error("e:", e);
        }
    }

    private Map<String, Object> bindParam(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>();

        //获取方法的参数名
        String[] params = new DefaultParameterNameDiscoverer().getParameterNames(method);

        //将参数名与参数值对应起来
        for (int len = 0; len < params.length; len++) {
            map.put(params[len], args[len]);
        }
        return map;
    }

    private Method getMethod(JoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Method targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        return targetMethod;
    }

    private String resolve(String value) {
        return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
    }
}
