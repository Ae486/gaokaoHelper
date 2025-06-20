package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 省份实体类
 * 对应数据库表：provinces
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "provinces")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province {

    /**
     * 省份ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INT COMMENT '省份ID, 主键'")
    private Integer id;

    /**
     * 省份名称, 如: 安徽省
     */
    @Column(name = "name", nullable = false, length = 100, 
            columnDefinition = "VARCHAR(100) NOT NULL COMMENT '省份名称, 如: 安徽省'")
    private String name;

    // 一对多关系：一个省份对应多个学校
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<School> schools;

    // 一对多关系：一个省份对应多个省份排名记录
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProvincialRanking> provincialRankings;

    // 一对多关系：一个省份对应多个录取分数记录
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdmissionScore> admissionScores;
}
