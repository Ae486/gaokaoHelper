package com.gaokao.helper.exception;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.common.Constants;
import com.gaokao.helper.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常 - @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("参数验证异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return Result.error(Constants.HttpStatus.BAD_REQUEST, errorMsg.toString());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.error("参数绑定异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return Result.error(Constants.HttpStatus.BAD_REQUEST, errorMsg.toString());
    }

    /**
     * 处理约束违反异常 - @Validated
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("约束违反异常: {} - {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getMessage()).append("; ");
        }
        
        return Result.error(Constants.HttpStatus.BAD_REQUEST, errorMsg.toString());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.error("认证异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(Constants.HttpStatus.UNAUTHORIZED, "认证失败，请重新登录");
    }

    /**
     * 处理凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("凭证错误异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(Constants.HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("访问拒绝异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(Constants.HttpStatus.FORBIDDEN, "访问被拒绝，权限不足");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(Constants.HttpStatus.BAD_REQUEST, "参数错误: " + e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(Constants.HttpStatus.INTERNAL_SERVER_ERROR, "系统内部错误");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(Constants.HttpStatus.INTERNAL_SERVER_ERROR, "系统异常，请稍后重试");
    }
}
