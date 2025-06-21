package com.gaokao.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户注册响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 构造方法
     */
    public static RegisterResponse of(Long userId, String username, String email, String realName, LocalDateTime createdAt) {
        return new RegisterResponse(userId, username, email, realName, createdAt);
    }
}
