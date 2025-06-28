package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * MBTI测试题实体类
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Entity
@Table(name = "mbti_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MbtiQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 题目内容
     */
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /**
     * 选项A
     */
    @Column(name = "option_a", nullable = false)
    private String optionA;

    /**
     * 选项B
     */
    @Column(name = "option_b", nullable = false)
    private String optionB;

    /**
     * 测试维度：EI(外向-内向), SN(感觉-直觉), TF(思考-情感), JP(判断-感知)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "dimension", nullable = false)
    private MbtiDimension dimension;

    /**
     * 选择A的得分
     */
    @Column(name = "a_score", nullable = false)
    private Integer aScore;

    /**
     * 选择B的得分
     */
    @Column(name = "b_score", nullable = false)
    private Integer bScore;

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
     * MBTI维度枚举
     */
    public enum MbtiDimension {
        EI, // 外向-内向
        SN, // 感觉-直觉
        TF, // 思考-情感
        JP  // 判断-感知
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
