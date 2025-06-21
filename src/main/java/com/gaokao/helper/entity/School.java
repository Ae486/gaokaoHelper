package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 院校信息实体类
 * 对应数据库表：schools
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "schools")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {

    /**
     * 学校ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INT COMMENT '学校ID, 主键'")
    private Integer id;

    /**
     * 学校官方名称
     */
    @Column(name = "name", nullable = false, length = 255, 
            columnDefinition = "VARCHAR(255) NOT NULL COMMENT '学校官方名称'")
    private String name;

    /**
     * 学校所在省份ID, 关联provinces表
     */
    @Column(name = "province_id", columnDefinition = "INT COMMENT '学校所在省份ID, 关联provinces表'")
    private Integer provinceId;

    /**
     * 办学层次, 如: 本科, 专科
     */
    @Column(name = "schoolLevel", length = 50, 
            columnDefinition = "VARCHAR(50) COMMENT '办学层次, 如: 本科, 专科'")
    private String schoolLevel;

    /**
     * 隶属单位, 如: 教育部
     */
    @Column(name = "schoolAffiliation", length = 255, 
            columnDefinition = "VARCHAR(255) COMMENT '隶属单位, 如: 教育部'")
    private String schoolAffiliation;

    /**
     * 办学性质, 如: 公办, 民办
     */
    @Column(name = "ownership_type", length = 50, 
            columnDefinition = "VARCHAR(50) COMMENT '办学性质, 如: 公办, 民办'")
    private String ownershipType;

    /**
     * 院校类型, 如: 综合, 理工, 师范
     */
    @Column(name = "school_type", length = 50, 
            columnDefinition = "VARCHAR(50) COMMENT '院校类型, 如: 综合, 理工, 师范'")
    private String schoolType;

    /**
     * 院校标签, 如 "985,211,双一流", 以逗号分隔
     */
    @Column(name = "tags", columnDefinition = "TEXT COMMENT '院校标签, 如 \"985,211,双一流\", 以逗号分隔'")
    private String tags;

    /**
     * 大学简介
     */
    @Column(name = "schoolIntroduction", columnDefinition = "TEXT COMMENT '大学简介'")
    private String schoolIntroduction;

    /**
     * 特色专业
     */
    @Column(name = "schoolNationalProfessions", columnDefinition = "TEXT COMMENT '特色专业'")
    private String schoolNationalProfessions;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
