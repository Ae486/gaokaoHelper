package com.gaokao.helper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证入口点
 * 处理未认证的请求
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.error("未认证访问: {} {}", request.getMethod(), request.getRequestURI());
        
        // 设置响应状态码和内容类型
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        // 构建错误响应
        Result<Object> result = Result.error(401, "未授权访问，请先登录");
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
