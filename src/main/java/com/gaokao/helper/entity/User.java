package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 * 简化版本，只包含核心字段：id, username, password, createdAt
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 用户名, 唯一（区分大小写）
     */
    @Column(name = "username", nullable = false, unique = true, length = 50,
            columnDefinition = "VARCHAR(50) COLLATE utf8_bin")
    private String username;

    /**
     * 加密后的密码
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 账户创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
