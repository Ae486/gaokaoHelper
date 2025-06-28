package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 霍兰德测试题实体类
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Entity
@Table(name = "holland_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HollandQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 题目内容
     */
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /**
     * 题目类型：活动兴趣、能力自评、职业兴趣
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    /**
     * 兴趣维度：R(现实型), I(研究型), A(艺术型), S(社会型), E(企业型), C(常规型)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "dimension", nullable = false)
    private HollandDimension dimension;

    /**
     * 题目顺序
     */
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 题目类型枚举
     */
    public enum QuestionType {
        ACTIVITY,   // 活动兴趣
        ABILITY,    // 能力自评
        OCCUPATION  // 职业兴趣
    }

    /**
     * 霍兰德维度枚举
     */
    public enum HollandDimension {
        R, // 现实型
        I, // 研究型
        A, // 艺术型
        S, // 社会型
        E, // 企业型
        C  // 常规型
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
