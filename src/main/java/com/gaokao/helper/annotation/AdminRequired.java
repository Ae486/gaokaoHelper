package com.gaokao.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员权限注解
 * 标记需要管理员权限才能访问的方法
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminRequired {
    
    /**
     * 权限描述
     */
    String value() default "需要管理员权限";
    
    /**
     * 是否记录操作日志
     */
    boolean logOperation() default true;
}
