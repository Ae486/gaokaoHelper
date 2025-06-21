package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 大学排名实体类
 * 对应数据库表：university_rankings
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "university_rankings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversityRanking {

    /**
     * 排名记录ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INT COMMENT '排名记录ID, 主键'")
    private Integer id;

    /**
     * 学校ID, 关联schools表
     */
    @Column(name = "school_id", nullable = false, columnDefinition = "INT NOT NULL COMMENT '学校ID, 关联schools表'")
    private Integer schoolId;

    /**
     * 排名机构, 如: 软科, QS
     */
    @Column(name = "provider", nullable = false, length = 50, 
            columnDefinition = "VARCHAR(50) NOT NULL COMMENT '排名机构, 如: 软科, QS'")
    private String provider;

    /**
     * 排名年份
     */
    @Column(name = "year", nullable = false, columnDefinition = "INT NOT NULL COMMENT '排名年份'")
    private Integer year;

    /**
     * 排名
     */
    @Column(name = "rank", columnDefinition = "INT COMMENT '排名'")
    private Integer rank;

    /**
     * 综合得分
     */
    @Column(name = "score", columnDefinition = "FLOAT COMMENT '综合得分'")
    private Float score;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
