package com.gaokao.helper.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 专业信息实体类
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Entity
@Table(name = "majors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 专业名称
     */
    @Column(name = "major_name", nullable = false, length = 100)
    private String majorName;

    /**
     * 专业代码
     */
    @Column(name = "major_code", length = 20)
    private String majorCode;

    /**
     * 专业类别
     */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /**
     * 专业描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 就业前景
     */
    @Column(name = "career_prospects", columnDefinition = "TEXT")
    private String careerProspects;

    /**
     * 核心课程
     */
    @Column(name = "core_courses", columnDefinition = "TEXT")
    private String coreCourses;

    /**
     * 技能要求
     */
    @Column(name = "skill_requirements", columnDefinition = "TEXT")
    private String skillRequirements;

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
