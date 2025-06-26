package com.gaokao.helper.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.annotation.AdminRequired;
import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.entity.AdminLog;
import com.gaokao.helper.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 管理员权限切面
 * 处理@AdminRequired注解的权限验证和日志记录
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAspect {

    private final AdminLogRepository adminLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * 管理员用户名常量
     */
    private static final String ADMIN_USERNAME = "PLeiA";

    /**
     * 权限验证
     */
    @Before("@annotation(adminRequired)")
    public void checkAdminPermission(JoinPoint joinPoint, AdminRequired adminRequired) {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.unauthorized("请先登录");
        }

        String currentUsername = authentication.getName();
        
        // 检查是否为管理员
        if (!ADMIN_USERNAME.equals(currentUsername)) {
            log.warn("非管理员用户尝试访问管理功能: username={}, method={}", 
                    currentUsername, joinPoint.getSignature().getName());
            throw BusinessException.forbidden("您没有管理员权限");
        }

        log.debug("管理员权限验证通过: username={}, method={}", 
                currentUsername, joinPoint.getSignature().getName());
    }

    /**
     * 记录操作日志
     */
    @AfterReturning(value = "@annotation(adminRequired)", returning = "result")
    public void logAdminOperation(JoinPoint joinPoint, AdminRequired adminRequired, Object result) {
        if (!adminRequired.logOperation()) {
            return;
        }

        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = authentication.getName();

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            // 获取方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();

            // 创建日志记录
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminUsername(adminUsername);
            adminLog.setOperationType(determineOperationType(methodName));
            adminLog.setTableName(determineTableName(joinPoint));
            adminLog.setDescription(String.format("执行方法: %s", methodName));
            
            if (request != null) {
                adminLog.setIpAddress(getClientIpAddress(request));
                adminLog.setUserAgent(request.getHeader("User-Agent"));
            }

            // 记录参数和返回值
            try {
                if (joinPoint.getArgs().length > 0) {
                    adminLog.setNewData(objectMapper.writeValueAsString(joinPoint.getArgs()));
                }
            } catch (Exception e) {
                log.warn("记录操作参数失败: {}", e.getMessage());
            }

            adminLogRepository.save(adminLog);
            log.debug("管理员操作日志已记录: username={}, operation={}", adminUsername, methodName);

        } catch (Exception e) {
            log.error("记录管理员操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据方法名确定操作类型
     */
    private String determineOperationType(String methodName) {
        String lowerMethodName = methodName.toLowerCase();
        if (lowerMethodName.contains("create") || lowerMethodName.contains("add") || lowerMethodName.contains("save")) {
            return "CREATE";
        } else if (lowerMethodName.contains("update") || lowerMethodName.contains("edit") || lowerMethodName.contains("modify")) {
            return "UPDATE";
        } else if (lowerMethodName.contains("delete") || lowerMethodName.contains("remove")) {
            return "DELETE";
        } else {
            return "VIEW";
        }
    }

    /**
     * 根据连接点确定表名
     */
    private String determineTableName(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        if (className.contains("User")) {
            return "users";
        } else if (className.contains("School")) {
            return "schools";
        } else if (className.contains("AdmissionScore")) {
            return "admission_scores";
        } else if (className.contains("Province")) {
            return "provinces";
        } else {
            return "unknown";
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
