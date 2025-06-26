package com.gaokao.helper.repository;

import com.gaokao.helper.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员操作日志数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long>, JpaSpecificationExecutor<AdminLog> {

    /**
     * 根据管理员用户名查询操作日志
     */
    Page<AdminLog> findByAdminUsernameOrderByCreatedAtDesc(String adminUsername, Pageable pageable);

    /**
     * 根据操作类型查询日志
     */
    Page<AdminLog> findByOperationTypeOrderByCreatedAtDesc(String operationType, Pageable pageable);

    /**
     * 根据表名查询日志
     */
    Page<AdminLog> findByTableNameOrderByCreatedAtDesc(String tableName, Pageable pageable);

    /**
     * 根据时间范围查询日志
     */
    @Query("SELECT al FROM AdminLog al WHERE al.createdAt BETWEEN :startTime AND :endTime ORDER BY al.createdAt DESC")
    Page<AdminLog> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime, 
                                         Pageable pageable);

    /**
     * 查询最近的操作日志
     */
    List<AdminLog> findTop10ByOrderByCreatedAtDesc();

    /**
     * 统计某个管理员的操作次数
     */
    @Query("SELECT COUNT(al) FROM AdminLog al WHERE al.adminUsername = :adminUsername")
    long countByAdminUsername(@Param("adminUsername") String adminUsername);

    /**
     * 统计某种操作类型的次数
     */
    @Query("SELECT COUNT(al) FROM AdminLog al WHERE al.operationType = :operationType")
    long countByOperationType(@Param("operationType") String operationType);
}
