package com.gaokao.helper.repository;

import com.gaokao.helper.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 聊天会话数据访问接口
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    /**
     * 根据会话ID查找会话
     */
    Optional<ChatSession> findBySessionId(String sessionId);

    /**
     * 根据用户ID查找会话列表，按更新时间倒序
     */
    List<ChatSession> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, String status);

    /**
     * 根据用户ID分页查找会话列表
     */
    Page<ChatSession> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, String status, Pageable pageable);

    /**
     * 查找用户最近的会话
     */
    @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.status = :status " +
           "ORDER BY cs.updatedAt DESC")
    List<ChatSession> findRecentSessions(@Param("userId") Long userId, 
                                       @Param("status") String status, 
                                       Pageable pageable);

    /**
     * 统计用户的会话数量
     */
    long countByUserIdAndStatus(Long userId, String status);

    /**
     * 检查会话是否属于指定用户
     */
    boolean existsBySessionIdAndUserId(String sessionId, Long userId);
}
