package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * MBTI类型与专业匹配实体类
 * 
 * @author PLeiA
 * @since 2024-06-29
 */
@Entity
@Table(name = "mbti_major_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MbtiMajorMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * MBTI类型，如INTJ、ENFP等
     */
    @Column(name = "mbti_type", nullable = false, length = 4)
    private String mbtiType;

    /**
     * 专业ID
     */
    @Column(name = "major_id", nullable = false)
    private Integer majorId;

    /**
     * 匹配度分数 (0.0-1.0)
     */
    @Column(name = "match_score", nullable = false, precision = 3, scale = 2)
    private Double matchScore;

    /**
     * 匹配原因
     */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 关联的专业信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", insertable = false, updatable = false)
    private Major major;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
