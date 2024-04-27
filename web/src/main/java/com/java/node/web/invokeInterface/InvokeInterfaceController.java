package com.java.node.web.invokeInterface;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 利用反射，调用自定义bean的方法
 */
@Slf4j
@RestController
public class InvokeInterfaceController {

    @Autowired
    private ApplicationContext context;

    /**
     * 触发定时任务
     */
    @PostMapping("/invoke/interface")
    public String trigger(@RequestBody InvokeParam param) {
        if (ObjectUtil.hasEmpty(param, param.getClassName(), param.getMethodName())) {
            return "缺失参数";
        }
        String className = param.getClassName();
        String methodName = param.getMethodName();
        List<InvokeParam.Arg> argList = param.getArgList() == null ? new ArrayList<>() : param.getArgList();
        //bean对象默认的都是首字母小写
        className = className.substring(0, 1).toLowerCase() + className.substring(1);
        //获取bean对象
        Object bean;
        try {
            bean = context.getBean(className);
        } catch (Exception e) {
            log.info("类不存在,e:", e);
            return "类不存在";
        }
        Class<?>[] classTypeList = new Class[argList.size()];
        Object[] paramList = new Object[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            InvokeParam.Arg arg = argList.get(i);
            try {
                classTypeList[i] = ClassLoader.getSystemClassLoader().loadClass(arg.getClassType());
                if (arg.getValue() != null) {
                    paramList[i] = classTypeList[i] == String.class ? arg.getValue() : JSONObject.parseObject(arg.getValue().toString(), classTypeList[i]);
                }
            } catch (Exception e) {
                log.info("{}参数异常,arg:{},e:", i, arg, e);
                return "第" + i + "参数不正确";
            }
        }
        //获取方法
        Method method;
        try {
            method = bean.getClass().getMethod(methodName, classTypeList);
        } catch (NoSuchMethodException e) {
            log.info("方法不存在,e:", e);
            return "方法不存在";
        }
        //执行方法
        try {
            method.invoke(bean, paramList);
        } catch (Exception e) {
            log.error("反射调用方法异常,e:", e);
            return "error";
        }
        log.info("方法:{}.{}手动调用成功", className, methodName);
        return "success";
    }

    public void task1() {
        log.info("--------task1 execute-------------");
    }

    public void task2(String str) {
        log.info("--------task2 execute,str:{}-------------", str);
    }

    public void task3(Integer a, List<Integer> list) {
        log.info("--------task3 execute,a:{},list:{}-------------", a, JSONObject.toJSON(list));
    }

    public void task4(InvokeParam param, String str, Integer a, List<String> list) {
        log.info("--------task4 execute,param:{},str:{},a:{},list:{}-------------", JSONObject.toJSON(param), str, a, list);
    }

}
