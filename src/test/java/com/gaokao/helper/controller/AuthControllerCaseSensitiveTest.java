package com.gaokao.helper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户名大小写敏感性的控制器测试
 * 
 * @author PLeiA
 * @since 2024-06-23
 */
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerCaseSensitiveTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testCheckUsernameCaseSensitive() throws Exception {
        // Given - 模拟不同大小写的用户名存在性
        when(authService.existsByUsername("testuser")).thenReturn(true);   // 小写存在
        when(authService.existsByUsername("TESTUSER")).thenReturn(false);  // 大写不存在
        when(authService.existsByUsername("TestUser")).thenReturn(false);  // 混合不存在

        // When & Then - 测试小写用户名（已存在）
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(true))
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.data.message").value("用户名已存在"));

        // When & Then - 测试大写用户名（不存在）
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "TESTUSER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.data.message").value("用户名可用"));

        // When & Then - 测试混合大小写用户名（不存在）
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "TestUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.data.message").value("用户名可用"));
    }

    @Test
    void testRegisterWithDifferentCaseUsernames() throws Exception {
        // Given - 模拟服务层行为：不同大小写的用户名都不存在
        when(authService.existsByUsername(anyString())).thenReturn(false);

        // 创建不同大小写的注册请求
        RegisterRequest request1 = createRegisterRequest("testuser");
        RegisterRequest request2 = createRegisterRequest("TESTUSER");
        RegisterRequest request3 = createRegisterRequest("TestUser");

        // When & Then - 测试小写用户名注册
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // When & Then - 测试大写用户名注册
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // When & Then - 测试混合大小写用户名注册
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk());
    }

    @Test
    void testUsernameValidationCaseSensitive() throws Exception {
        // Given - 模拟已存在的用户名
        when(authService.existsByUsername("existinguser")).thenReturn(true);
        when(authService.existsByUsername("EXISTINGUSER")).thenReturn(false);

        // When & Then - 测试已存在的小写用户名
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(true));

        // When & Then - 测试对应的大写用户名（应该可用）
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "EXISTINGUSER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.available").value(true));
    }

    /**
     * 创建注册请求对象
     */
    private RegisterRequest createRegisterRequest(String username) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword("TestPass123");
        request.setConfirmPassword("TestPass123");
        return request;
    }
}
