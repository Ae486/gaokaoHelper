package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 省份一分一段实体类
 * 对应数据库表：provincial_rankings
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "provincial_rankings", 
       indexes = {
           @Index(name = "idx_rankings_query", 
                  columnList = "year, province_id, subject_category_id, score")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvincialRanking {

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
     * 省份ID, 关联provinces表
     */
    @Column(name = "province_id", nullable = false, columnDefinition = "INT NOT NULL COMMENT '省份ID, 关联provinces表'")
    private Integer provinceId;

    /**
     * 科类ID, 关联subject_categories表
     */
    @Column(name = "subject_category_id", nullable = false, 
            columnDefinition = "INT NOT NULL COMMENT '科类ID, 关联subject_categories表'")
    private Integer subjectCategoryId;

    /**
     * 分数
     */
    @Column(name = "score", nullable = false, columnDefinition = "INT NOT NULL COMMENT '分数'")
    private Integer score;

    /**
     * 本段人数
     */
    @Column(name = "count_at_score", columnDefinition = "INT COMMENT '本段人数'")
    private Integer countAtScore;

    /**
     * 累计人数 (即排名)
     */
    @Column(name = "cumulative_count", nullable = false, columnDefinition = "INT NOT NULL COMMENT '累计人数 (即排名)'")
    private Integer cumulativeCount;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
