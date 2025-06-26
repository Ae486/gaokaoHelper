package com.gaokao.helper.repository;

import com.gaokao.helper.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository 集成测试
 * 测试用户名大小写敏感性
 *
 * @author PLeiA
 * @since 2024-06-23
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUsernameCaseSensitivity() {
        // Given - 创建三个不同大小写的用户
        User user1 = createUser("testuser", "test1@example.com");
        User user2 = createUser("TESTUSER", "test2@example.com");
        User user3 = createUser("TestUser", "test3@example.com");

        // When - 保存用户
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        entityManager.flush();
        entityManager.clear();

        // Then - 验证所有用户都能成功保存
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotNull(savedUser3.getId());

        // 验证用户名完全匹配查询
        Optional<User> foundUser1 = userRepository.findByUsername("testuser");
        Optional<User> foundUser2 = userRepository.findByUsername("TESTUSER");
        Optional<User> foundUser3 = userRepository.findByUsername("TestUser");

        assertTrue(foundUser1.isPresent());
        assertTrue(foundUser2.isPresent());
        assertTrue(foundUser3.isPresent());

        assertEquals("testuser", foundUser1.get().getUsername());
        assertEquals("TESTUSER", foundUser2.get().getUsername());
        assertEquals("TestUser", foundUser3.get().getUsername());

        // 验证大小写不匹配时找不到用户
        Optional<User> notFound1 = userRepository.findByUsername("TESTUSER"); // 查找大写，但应该找到大写的用户
        Optional<User> notFound2 = userRepository.findByUsername("testuser"); // 查找小写，但应该找到小写的用户
        Optional<User> notFound3 = userRepository.findByUsername("testUSER"); // 混合大小写，应该找不到

        assertTrue(notFound1.isPresent()); // 应该找到 TESTUSER
        assertTrue(notFound2.isPresent()); // 应该找到 testuser
        assertFalse(notFound3.isPresent()); // 应该找不到 testUSER
    }

    @Test
    void testExistsByUsernameCaseSensitivity() {
        // Given - 保存一个用户
        User user = createUser("TestUser", "test@example.com");
        userRepository.save(user);
        entityManager.flush();

        // When & Then - 验证存在性检查的大小写敏感性
        assertTrue(userRepository.existsByUsername("TestUser"));   // 完全匹配
        assertFalse(userRepository.existsByUsername("testuser"));  // 小写不匹配
        assertFalse(userRepository.existsByUsername("TESTUSER"));  // 大写不匹配
        assertFalse(userRepository.existsByUsername("testUSER"));  // 混合不匹配
    }

    @Test
    void testMultipleUsersWithSimilarUsernames() {
        // Given - 创建多个相似但大小写不同的用户名
        String[] usernames = {"abc", "ABC", "Abc", "aBc", "abC"};
        
        // When - 保存所有用户
        for (int i = 0; i < usernames.length; i++) {
            User user = createUser(usernames[i], "test" + i + "@example.com");
            userRepository.save(user);
        }
        entityManager.flush();

        // Then - 验证所有用户都能独立存在
        for (String username : usernames) {
            assertTrue(userRepository.existsByUsername(username), 
                "用户名 " + username + " 应该存在");
            
            Optional<User> found = userRepository.findByUsername(username);
            assertTrue(found.isPresent(), 
                "应该能找到用户名 " + username);
            assertEquals(username, found.get().getUsername(), 
                "找到的用户名应该完全匹配");
        }

        // 验证总共有5个不同的用户
        long totalUsers = userRepository.count();
        assertEquals(5, totalUsers, "应该有5个不同的用户");
    }

    /**
     * 创建测试用户
     */
    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("$2a$12$encrypted.password.hash");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
