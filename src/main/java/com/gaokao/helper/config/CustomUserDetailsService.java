package com.gaokao.helper.config;

import com.gaokao.helper.entity.User;
import com.gaokao.helper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * 自定义用户详情服务
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户详情: username={}", username);
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.error("用户不存在: username={}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        User user = userOptional.get();
        
        return new CustomUserPrincipal(user);
    }

    /**
     * 自定义用户主体类
     */
    public static class CustomUserPrincipal implements UserDetails {
        
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // 目前所有用户都是普通用户角色
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        /**
         * 获取用户实体
         */
        public User getUser() {
            return user;
        }

        /**
         * 获取用户ID
         */
        public Long getUserId() {
            return user.getId();
        }
    }
}
