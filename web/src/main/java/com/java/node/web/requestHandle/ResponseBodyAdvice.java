package com.java.node.web.requestHandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Description 将反参的数据进行修改
 */
@Slf4j
@RestControllerAdvice
public class ResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice {

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof RequestHandleResponse)) {
            log.info("不支持的反参类型:{}", body);
            return body;
        }
        RequestHandleResponse result = (RequestHandleResponse) body;
        result.setTs(System.currentTimeMillis());
        result.setSign("sign:" + result.getData());
        log.info("反参处理完成:{}", result);
        return result;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestURI = requestAttributes.getRequest().getRequestURI();
        if (requestURI != null && requestURI.startsWith("/request/handle")) {
            return true;
        }
        return false;
    }
}
