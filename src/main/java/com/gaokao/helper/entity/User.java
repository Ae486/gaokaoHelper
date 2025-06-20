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
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT COMMENT '用户ID, 主键'")
    private Long id;

    /**
     * 用户名, 唯一
     */
    @Column(name = "username", nullable = false, unique = true, length = 50, 
            columnDefinition = "VARCHAR(50) NOT NULL COMMENT '用户名, 唯一'")
    private String username;

    /**
     * 加密后的密码
     */
    @Column(name = "password", nullable = false, length = 255, 
            columnDefinition = "VARCHAR(255) NOT NULL COMMENT '加密后的密码'")
    private String password;

    /**
     * 用户昵称
     */
    @Column(name = "nickname", length = 50, columnDefinition = "VARCHAR(50) NULL COMMENT '用户昵称'")
    private String nickname;

    /**
     * 账户创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, 
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间'")
    private LocalDateTime createdAt;
}
