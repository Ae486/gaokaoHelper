package com.gaokao.helper.repository;

import com.gaokao.helper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据创建时间范围查找用户
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    List<User> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找最近注册的用户
     * 
     * @param limit 限制数量
     * @return 用户列表
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(@Param("limit") int limit);

    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    @Query("SELECT COUNT(u) FROM User u")
    Long countTotalUsers();

    /**
     * 统计指定时间段内注册的用户数
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startTime AND :endTime")
    Long countUsersByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
}
