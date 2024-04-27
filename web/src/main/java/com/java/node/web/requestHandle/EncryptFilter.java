package com.java.node.web.requestHandle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

/**
 * 通过过滤器实现对请求参数的校验，与修改
 */
@Slf4j
@WebFilter(urlPatterns = {"/request/handle/*"})
public class EncryptFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        JSONObject requestParam = getRequestParam(request);
        //判断签名
        String sign = requestParam.getString("sign");
        if (StrUtil.isBlank(sign)) {
            log.info("签名错误");
            try (OutputStream out = response.getOutputStream()) {
                out.write("SIGN ERROR".getBytes());
            }
            return;
        }
        //修改内容
        requestParam.put("sign", "pass=" + sign);
        filterChain.doFilter(new RequestWrapper(request, requestParam), response);
    }

    @SneakyThrows
    private JSONObject getRequestParam(HttpServletRequest request) {
        JSONObject requestParam = new JSONObject();
        if ("GET".equals(request.getMethod())) {
            //获取get请求的参数
            request.getParameterMap().forEach((key, value) -> {
                String v = (value == null || value.length == 0) ? null : value[0];
                requestParam.put(key, v);
            });
            log.info("这是一个get请求,参数:{}", requestParam);
        } else {
            String contentType = request.getContentType();
            if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    || StringUtils.substringMatch(contentType, 0, MediaType.MULTIPART_FORM_DATA_VALUE)) {
                //获取form表单请求的参数
                Enumeration<String> parameterNames = request.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String name = parameterNames.nextElement();
                    requestParam.put(name, request.getParameter(name));
                }
                log.info("这是一个postForm请求,参数:{}", requestParam);
            } else if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE)) {
                //获取JSON的数据
                String string = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                requestParam.putAll(JSONObject.parseObject(string));
                log.info("这是一个postJSON请求,参数:{}", requestParam);
            } else {
                log.info("这是一个未知请求");
            }
        }
        return requestParam;
    }

    static class RequestWrapper extends HttpServletRequestWrapper {

        private final JSONObject body;

        public RequestWrapper(HttpServletRequest request, JSONObject requestParam) {
            super(request);
            this.body = requestParam;
        }

        /**
         * get请求，post请求的form表单的请求方式，会调用此方法获取参数的key
         *
         * @return
         */
        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(body.keySet());
        }

        /**
         * get请求，post请求的form表单的请求方式，会调用此方法通过key获取对应的value
         *
         * @param parameter
         * @return
         */
        @Override
        public String[] getParameterValues(String parameter) {
            return new String[]{body.getString(parameter)};
        }

        /**
         * post-RequestBody会调用此方法获取body的内容，所以重写此方法，返回我们处理过后的body
         *
         * @return
         */
        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.toString().getBytes(StandardCharsets.UTF_8));

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

    }
}



