package com.gaokao.helper.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.code = Constants.HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 创建参数错误异常
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(Constants.HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 创建请求错误异常
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(Constants.HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 创建未授权异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(Constants.HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 创建禁止访问异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(Constants.HttpStatus.FORBIDDEN, message);
    }

    /**
     * 创建资源不存在异常
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(Constants.HttpStatus.NOT_FOUND, message);
    }

    /**
     * 创建系统异常
     */
    public static BusinessException systemError(String message) {
        return new BusinessException(Constants.HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
