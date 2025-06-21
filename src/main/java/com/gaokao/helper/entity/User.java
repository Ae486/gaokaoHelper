package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 用户名, 唯一
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 加密后的密码
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 手机号
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 高考年份
     */
    @Column(name = "exam_year")
    private Integer examYear;

    /**
     * 高考总分
     */
    @Column(name = "total_score")
    private Integer totalScore;

    /**
     * 省份ID
     */
    @Column(name = "province_id")
    private Integer provinceId;

    /**
     * 科类ID
     */
    @Column(name = "subject_type_id")
    private Integer subjectTypeId;

    /**
     * 账户创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 账户更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 多对一关系 - 省份
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", insertable = false, updatable = false)
    private Province province;

    // 多对一关系 - 科类
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_type_id", insertable = false, updatable = false)
    private SubjectCategory subjectCategory;
}
