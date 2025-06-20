package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 科类实体类
 * 对应数据库表：subject_categories
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "subject_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCategory {

    /**
     * 科类ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INT COMMENT '科类ID, 主键'")
    private Integer id;

    /**
     * 科类名称, 如: 物理组, 历史组, 文科, 理科
     */
    @Column(name = "name", nullable = false, length = 100, 
            columnDefinition = "VARCHAR(100) NOT NULL COMMENT '科类名称, 如: 物理组, 历史组, 文科, 理科'")
    private String name;

    // 一对多关系：一个科类对应多个省份排名记录
    @OneToMany(mappedBy = "subjectCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProvincialRanking> provincialRankings;

    // 一对多关系：一个科类对应多个录取分数记录
    @OneToMany(mappedBy = "subjectCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdmissionScore> admissionScores;
}
