package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * MBTI类型描述实体类
 * 
 * @author PLeiA
 * @since 2024-06-29
 */
@Entity
@Table(name = "mbti_descriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MbtiDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * MBTI类型，如INTJ、ENFP等
     */
    @Column(name = "mbti_type", nullable = false, length = 4)
    private String mbtiType;

    /**
     * 类型名称，如"建筑师"、"思想家"等
     */
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    /**
     * 类型描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 优势特点
     */
    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    /**
     * 发展建议/弱点
     */
    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    /**
     * 著名代表人物
     */
    @Column(name = "famous_people", columnDefinition = "TEXT")
    private String famousPeople;

    /**
     * 适合的职业
     */
    @Column(name = "suitable_careers", columnDefinition = "TEXT")
    private String suitableCareers;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
