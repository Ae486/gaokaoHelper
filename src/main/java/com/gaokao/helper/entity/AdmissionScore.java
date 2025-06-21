package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 历年专业录取分数实体类
 * 对应数据库表：admission_scores
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "admission_scores", 
       indexes = {
           @Index(name = "idx_scores_query", 
                  columnList = "year, school_id, province_id, subject_category_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionScore {

    /**
     * 记录ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT COMMENT '记录ID, 主键'")
    private Long id;

    /**
     * 年份
     */
    @Column(name = "year", nullable = false, columnDefinition = "INT NOT NULL COMMENT '年份'")
    private Integer year;

    /**
     * 学校ID, 关联schools表
     */
    @Column(name = "school_id", nullable = false, columnDefinition = "INT NOT NULL COMMENT '学校ID, 关联schools表'")
    private Integer schoolId;

    /**
     * 考生生源地省份ID, 关联provinces表
     */
    @Column(name = "province_id", nullable = false, 
            columnDefinition = "INT NOT NULL COMMENT '考生生源地省份ID, 关联provinces表'")
    private Integer provinceId;

    /**
     * 科类ID, 关联subject_categories表
     */
    @Column(name = "subject_category_id", nullable = false, 
            columnDefinition = "INT NOT NULL COMMENT '科类ID, 关联subject_categories表'")
    private Integer subjectCategoryId;

    /**
     * 最低录取分
     */
    @Column(name = "min_score", columnDefinition = "FLOAT COMMENT '最低录取分'")
    private Float minScore;

    /**
     * 最低分位次
     */
    @Column(name = "min_rank", columnDefinition = "INT COMMENT '最低分位次'")
    private Integer minRank;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
