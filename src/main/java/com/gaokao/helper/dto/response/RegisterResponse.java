package com.gaokao.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户注册响应DTO
 * 简化版本，只包含核心字段：userId, username, createdAt
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
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 构造方法
     */
    public static RegisterResponse of(Long userId, String username, LocalDateTime createdAt) {
        return new RegisterResponse(userId, username, createdAt);
    }
}
