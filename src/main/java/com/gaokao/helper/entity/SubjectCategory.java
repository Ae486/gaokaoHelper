package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 科类名称, 如: 物理组, 历史组, 文科, 理科
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 暂时移除关联关系，避免循环引用问题
    // TODO: 后续可以根据需要添加关联关系
}
