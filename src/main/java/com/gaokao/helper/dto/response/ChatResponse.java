package com.gaokao.helper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天响应DTO
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * AI回复内容
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime timestamp;

    /**
     * 建议问题列表
     */
    private List<String> suggestions;

    /**
     * 是否为新会话
     */
    private Boolean isNewSession;

    /**
     * 会话标题
     */
    private String sessionTitle;
}
