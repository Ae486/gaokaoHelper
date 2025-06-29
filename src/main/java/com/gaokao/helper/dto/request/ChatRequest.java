package com.gaokao.helper.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 聊天请求DTO
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Data
public class ChatRequest {

    /**
     * 会话ID（可选，如果不提供则创建新会话）
     */
    private String sessionId;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000个字符")
    private String message;

    /**
     * 是否需要上下文（默认true）
     */
    private Boolean useContext = true;

    /**
     * 上下文消息数量（默认10条）
     */
    private Integer contextSize = 10;
}
