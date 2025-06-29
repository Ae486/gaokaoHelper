package com.gaokao.helper.service;

import com.gaokao.helper.dto.request.ChatRequest;
import com.gaokao.helper.dto.response.ChatResponse;
import com.gaokao.helper.dto.response.ChatSessionResponse;
import com.gaokao.helper.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 聊天服务接口
 *
 * @author PLeiA
 * @since 2024-06-29
 */
public interface ChatService {

    /**
     * 创建新的聊天会话
     */
    ChatSessionResponse createSession(Long userId);

    /**
     * 发送消息并获取AI回复
     */
    ChatResponse sendMessage(Long userId, ChatRequest request);

    /**
     * 获取用户的聊天会话列表
     */
    List<ChatSessionResponse> getUserSessions(Long userId);

    /**
     * 分页获取用户的聊天会话列表
     */
    Page<ChatSessionResponse> getUserSessions(Long userId, Pageable pageable);

    /**
     * 获取会话的聊天历史
     */
    List<ChatMessage> getChatHistory(String sessionId, Long userId);

    /**
     * 分页获取会话的聊天历史
     */
    Page<ChatMessage> getChatHistory(String sessionId, Long userId, Pageable pageable);

    /**
     * 删除聊天会话
     */
    void deleteSession(String sessionId, Long userId);

    /**
     * 更新会话标题
     */
    void updateSessionTitle(String sessionId, Long userId, String title);

    /**
     * 归档会话
     */
    void archiveSession(String sessionId, Long userId);

    /**
     * 生成测试报告
     * 用于性格测试的详细报告生成
     */
    String generateReport(String prompt);
}
