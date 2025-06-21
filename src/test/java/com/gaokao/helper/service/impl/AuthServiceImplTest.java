package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.request.LoginRequest;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.dto.response.LoginResponse;
import com.gaokao.helper.dto.response.RegisterResponse;
import com.gaokao.helper.entity.User;
import com.gaokao.helper.repository.UserRepository;
import com.gaokao.helper.util.JwtUtil;
import com.gaokao.helper.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService单元测试
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // 设置JWT过期时间
        ReflectionTestUtils.setField(authService, "jwtExpiration", 86400000L);

        // 准备测试数据
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("TestPass123");
        registerRequest.setConfirmPassword("TestPass123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");
        registerRequest.setRealName("测试用户");
        registerRequest.setProvinceId(1);
        registerRequest.setSubjectTypeId(1);
        registerRequest.setExamYear(2024);
        registerRequest.setTotalScore(600);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("TestPass123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        // 使用PasswordUtil编码密码以确保测试一致性
        user.setPassword(PasswordUtil.encodePassword("TestPass123"));
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setRealName("测试用户");
        user.setProvinceId(1);
        user.setSubjectTypeId(1);
        user.setExamYear(2024);
        user.setTotalScore(600);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegisterSuccess() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        RegisterResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getRealName(), response.getRealName());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByPhone("13800138000");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUsernameExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("用户名已存在", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterEmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterPasswordMismatch() {
        // Given
        registerRequest.setConfirmPassword("DifferentPass123");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(registerRequest));
        
        assertEquals("两次输入的密码不一致", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        // Given
        // 需要模拟PasswordUtil.matches方法返回true
        // 由于PasswordUtil是静态方法，这里简化测试，假设密码验证通过
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(Long.class), anyString())).thenReturn("mock-jwt-token");

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400000L, response.getExpiresIn());
        assertNotNull(response.getUserInfo());
        assertEquals(user.getId(), response.getUserInfo().getUserId());
        assertEquals(user.getUsername(), response.getUserInfo().getUsername());

        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).generateToken(user.getId(), user.getUsername());
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        
        assertEquals("用户名或密码错误", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil, never()).generateToken(any(Long.class), anyString());
    }

    @Test
    void testExistsByUsername() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When
        boolean exists = authService.existsByUsername("testuser");

        // Then
        assertTrue(exists);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void testExistsByEmail() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        boolean exists = authService.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void testExistsByEmailEmpty() {
        // When
        boolean exists = authService.existsByEmail("");

        // Then
        assertFalse(exists);
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void testExistsByPhone() {
        // Given
        when(userRepository.existsByPhone(anyString())).thenReturn(true);

        // When
        boolean exists = authService.existsByPhone("13800138000");

        // Then
        assertTrue(exists);
        verify(userRepository).existsByPhone("13800138000");
    }

    @Test
    void testExistsByPhoneEmpty() {
        // When
        boolean exists = authService.existsByPhone("");

        // Then
        assertFalse(exists);
        verify(userRepository, never()).existsByPhone(anyString());
    }
}
