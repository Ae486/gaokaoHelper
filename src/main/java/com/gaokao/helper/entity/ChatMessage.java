package com.gaokao.helper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * 对应数据库表：chat_messages
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * 消息ID, 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 会话ID
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    /**
     * 消息类型：user-用户消息, assistant-AI助手消息, system-系统消息
     */
    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    /**
     * 消息内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 消息角色（用于AI API）
     */
    @Column(name = "role", length = 20)
    private String role;

    /**
     * 消息状态：sent-已发送, delivered-已送达, failed-发送失败
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "sent";

    /**
     * 消息创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 关联的聊天会话
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private ChatSession chatSession;

    /**
     * 构造用户消息
     */
    public static ChatMessage createUserMessage(Long sessionId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setMessageType("user");
        message.setRole("user");
        message.setContent(content);
        message.setStatus("sent");
        return message;
    }

    /**
     * 构造AI助手消息
     */
    public static ChatMessage createAssistantMessage(Long sessionId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setMessageType("assistant");
        message.setRole("assistant");
        message.setContent(content);
        message.setStatus("sent");
        return message;
    }

    /**
     * 构造系统消息
     */
    public static ChatMessage createSystemMessage(Long sessionId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setMessageType("system");
        message.setRole("system");
        message.setContent(content);
        message.setStatus("sent");
        return message;
    }
}
