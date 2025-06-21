package com.gaokao.helper.config;

import com.gaokao.helper.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从请求头中获取JWT token
            String token = jwtUtil.getTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 从token中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);
                
                if (username != null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 验证token
                    if (jwtUtil.validateToken(token, username)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置认证信息到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("JWT认证成功: username={}", username);
                    } else {
                        log.debug("JWT token验证失败: username={}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器异常", e);
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
