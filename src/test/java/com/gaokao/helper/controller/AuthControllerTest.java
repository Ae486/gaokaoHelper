package com.gaokao.helper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.dto.request.LoginRequest;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.dto.response.LoginResponse;
import com.gaokao.helper.dto.response.RegisterResponse;
import com.gaokao.helper.service.AuthService;
import com.gaokao.helper.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController集成测试
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RegisterResponse registerResponse;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("TestPass123");
        registerRequest.setConfirmPassword("TestPass123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("TestPass123");

        registerResponse = RegisterResponse.of(
            1L, "testuser", LocalDateTime.now()
        );

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            1L, "testuser"
        );
        loginResponse = LoginResponse.of("mock-jwt-token", 86400000L, userInfo);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void testRegisterValidationError() throws Exception {
        // Given - 无效的注册请求（用户名太短）
        registerRequest.setUsername("ab");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.userInfo.userId").value(1))
                .andExpect(jsonPath("$.data.userInfo.username").value("testuser"));
    }

    @Test
    void testLoginValidationError() throws Exception {
        // Given - 无效的登录请求（密码为空）
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckUsernameAvailable() throws Exception {
        // Given
        when(authService.existsByUsername(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.data.message").value("用户名可用"));
    }

    @Test
    void testCheckUsernameExists() throws Exception {
        // Given
        when(authService.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(true))
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.message").value("用户名已存在"));
    }

}
