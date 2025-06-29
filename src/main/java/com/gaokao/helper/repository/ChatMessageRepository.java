package com.gaokao.helper.repository;

import com.gaokao.helper.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息数据访问接口
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 根据会话ID查找消息列表，按创建时间正序
     */
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    /**
     * 根据会话ID分页查找消息列表
     */
    Page<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId, Pageable pageable);

    /**
     * 查找会话的最近消息
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessages(@Param("sessionId") Long sessionId, Pageable pageable);

    /**
     * 统计会话的消息数量
     */
    long countBySessionId(Long sessionId);

    /**
     * 查找会话的上下文消息（最近N条）
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId " +
           "AND cm.messageType IN ('user', 'assistant') " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findContextMessages(@Param("sessionId") Long sessionId, Pageable pageable);

    /**
     * 删除会话的所有消息
     */
    void deleteBySessionId(Long sessionId);
}
