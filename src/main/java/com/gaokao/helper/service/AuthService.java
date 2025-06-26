package com.gaokao.helper.service;

import com.gaokao.helper.dto.request.LoginRequest;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.dto.response.LoginResponse;
import com.gaokao.helper.dto.response.RegisterResponse;

/**
 * 用户认证服务接口
 *
 * @author PLeiA
 * @since 2024-06-20
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
