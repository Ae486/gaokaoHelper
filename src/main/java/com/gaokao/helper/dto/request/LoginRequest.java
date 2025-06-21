package com.gaokao.helper.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求DTO
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    private String password;

    /**
     * 记住我（可选）
     */
    private Boolean rememberMe = false;
}
