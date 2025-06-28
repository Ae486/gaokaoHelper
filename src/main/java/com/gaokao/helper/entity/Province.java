package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 省份名称, 如: 安徽省
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 省份考试类型
     * 0: 新高考3+3模式 (浙江、上海等)
     * 1: 新高考3+1+2模式或传统文理分科 (大部分省份)
     * 2: 其他特殊模式
     */
    @Column(name = "provinceExamType")
    private Integer provinceExamType;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
