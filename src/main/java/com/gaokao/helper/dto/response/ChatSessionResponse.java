package com.gaokao.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天会话响应DTO
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 消息数量
     */
    private Long messageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
