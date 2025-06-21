package com.gaokao.helper.controller;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.LoginRequest;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.dto.response.LoginResponse;
import com.gaokao.helper.dto.response.RegisterResponse;
import com.gaokao.helper.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "用户认证", description = "用户注册、登录相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("用户注册请求: username={}", request.getUsername());
            RegisterResponse response = authService.register(request);
            return Result.success("注册成功", response);
        } catch (BusinessException e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("用户登录请求: username={}", request.getUsername());
            LoginResponse response = authService.login(request);
            return Result.success("登录成功", response);
        } catch (BusinessException e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名", description = "检查用户名是否已被注册")
    public Result<Map<String, Object>> checkUsername(@RequestParam String username) {
        try {
            boolean exists = authService.existsByUsername(username);
            Map<String, Object> data = new HashMap<>();
            data.put("exists", exists);
            data.put("available", !exists);
            data.put("message", exists ? "用户名已存在" : "用户名可用");

            return Result.success(data);
        } catch (Exception e) {
            log.error("检查用户名异常", e);
            return Result.error("检查失败，请稍后重试");
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已被注册")
    public Result<Map<String, Object>> checkEmail(@RequestParam String email) {
        try {
            boolean exists = authService.existsByEmail(email);
            Map<String, Object> data = new HashMap<>();
            data.put("exists", exists);
            data.put("available", !exists);
            data.put("message", exists ? "邮箱已被注册" : "邮箱可用");

            return Result.success(data);
        } catch (Exception e) {
            log.error("检查邮箱异常", e);
            return Result.error("检查失败，请稍后重试");
        }
    }

    /**
     * 检查手机号是否可用
     */
    @GetMapping("/check-phone")
    @Operation(summary = "检查手机号", description = "检查手机号是否已被注册")
    public Result<Map<String, Object>> checkPhone(@RequestParam String phone) {
        try {
            boolean exists = authService.existsByPhone(phone);
            Map<String, Object> data = new HashMap<>();
            data.put("exists", exists);
            data.put("available", !exists);
            data.put("message", exists ? "手机号已被注册" : "手机号可用");

            return Result.success(data);
        } catch (Exception e) {
            log.error("检查手机号异常", e);
            return Result.error("检查失败，请稍后重试");
        }
    }

    /**
     * 获取密码强度要求
     */
    @GetMapping("/password-requirements")
    @Operation(summary = "密码要求", description = "获取密码强度要求说明")
    public Result<Map<String, String>> getPasswordRequirements() {
        Map<String, String> data = new HashMap<>();
        data.put("requirements", "密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字");
        data.put("pattern", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$");
        return Result.success(data);
    }
}
