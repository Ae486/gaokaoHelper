package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 性格测试记录实体类
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Entity
@Table(name = "personality_test_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityTestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户ID（可为空，支持匿名测试）
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 测试类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private TestType testType;

    /**
     * 测试结果
     */
    @Column(name = "test_result", nullable = false, length = 10)
    private String testResult;

    /**
     * 详细得分（JSON格式）
     */
    @Column(name = "detailed_scores", columnDefinition = "JSON")
    private String detailedScores;

    /**
     * 测试时长（秒）
     */
    @Column(name = "test_duration")
    private Integer testDuration;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 测试类型枚举
     */
    public enum TestType {
        MBTI,    // MBTI测试
        HOLLAND  // 霍兰德测试
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
