package com.gaokao.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户登录响应DTO
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT token
     */
    private String token;

    /**
     * token类型
     */
    private String tokenType = "Bearer";

    /**
     * token过期时间（毫秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 用户信息内部类
     * 简化版本，只包含核心字段：userId, username
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String username;
    }

    /**
     * 构造方法
     */
    public static LoginResponse of(String token, Long expiresIn, UserInfo userInfo) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        response.setUserInfo(userInfo);
        response.setLoginTime(LocalDateTime.now());
        return response;
    }
}
