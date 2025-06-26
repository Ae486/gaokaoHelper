package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 管理员操作日志实体类
 * 对应数据库表：admin_logs
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@Entity
@Table(name = "admin_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

    /**
     * 日志ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 操作管理员用户名
     */
    @Column(name = "admin_username", nullable = false, length = 50)
    private String adminUsername;

    /**
     * 操作类型 (CREATE, UPDATE, DELETE, VIEW)
     */
    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    /**
     * 操作的表名
     */
    @Column(name = "table_name", nullable = false, length = 50)
    private String tableName;

    /**
     * 操作的记录ID
     */
    @Column(name = "record_id")
    private Long recordId;

    /**
     * 操作描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 操作前的数据（JSON格式）
     */
    @Column(name = "old_data", columnDefinition = "TEXT")
    private String oldData;

    /**
     * 操作后的数据（JSON格式）
     */
    @Column(name = "new_data", columnDefinition = "TEXT")
    private String newData;

    /**
     * 客户端IP地址
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 用户代理信息
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 操作时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
