package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.request.LoginRequest;
import com.gaokao.helper.dto.request.RegisterRequest;
import com.gaokao.helper.dto.response.LoginResponse;
import com.gaokao.helper.dto.response.RegisterResponse;
import com.gaokao.helper.entity.User;
import com.gaokao.helper.repository.UserRepository;
import com.gaokao.helper.service.AuthService;
import com.gaokao.helper.util.JwtUtil;
import com.gaokao.helper.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户认证服务实现类
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());

        // 1. 参数验证
        validateRegisterRequest(request);

        // 2. 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw BusinessException.paramError("用户名已存在");
        }

        // 注意：邮箱和手机号为可选字段，不进行重复检查

        // 3. 创建用户实体
        User user = createUserFromRequest(request);

        // 4. 保存用户
        User savedUser = userRepository.save(user);

        log.info("用户注册成功: userId={}, username={}", savedUser.getId(), savedUser.getUsername());

        // 5. 构建响应
        return RegisterResponse.of(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRealName(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());

        // 1. 查找用户
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        User user = userOptional.get();

        // 2. 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        // 3. 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 4. 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRealName(),
                user.getProvinceId(),
                user.getSubjectTypeId(),
                user.getExamYear(),
                user.getTotalScore()
        );

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 5. 构建响应
        return LoginResponse.of(token, jwtExpiration, userInfo);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByPhone(phone);
    }

    /**
     * 验证注册请求参数
     */
    private void validateRegisterRequest(RegisterRequest request) {
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.paramError("两次输入的密码不一致");
        }

        // 验证密码强度
        if (!PasswordUtil.isValidPassword(request.getPassword())) {
            throw BusinessException.paramError(PasswordUtil.getPasswordRequirements());
        }
    }

    /**
     * 从注册请求创建用户实体
     */
    private User createUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encodePassword(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setProvinceId(request.getProvinceId());
        user.setSubjectTypeId(request.getSubjectTypeId());
        user.setExamYear(request.getExamYear());
        user.setTotalScore(request.getTotalScore());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
