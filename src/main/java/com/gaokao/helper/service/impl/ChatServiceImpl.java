package com.gaokao.helper.service.impl;

import cn.hutool.core.util.IdUtil;
import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.request.ChatRequest;
import com.gaokao.helper.dto.response.ChatResponse;
import com.gaokao.helper.dto.response.ChatSessionResponse;
import com.gaokao.helper.entity.ChatMessage;
import com.gaokao.helper.entity.ChatSession;
import com.gaokao.helper.repository.ChatMessageRepository;
import com.gaokao.helper.repository.ChatSessionRepository;
import com.gaokao.helper.service.ChatService;
import com.gaokao.helper.util.DeepSeekApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DeepSeekApiClient deepSeekApiClient;

    @Override
    @Transactional
    public ChatSessionResponse createSession(Long userId) {
        // 生成唯一会话ID
        String sessionId = IdUtil.simpleUUID();
        
        // 创建新会话
        ChatSession session = new ChatSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setTitle("新对话");
        session.setStatus("active");
        
        session = chatSessionRepository.save(session);
        
        log.info("为用户{}创建新聊天会话: {}", userId, sessionId);
        
        return ChatSessionResponse.builder()
                .sessionId(session.getSessionId())
                .title(session.getTitle())
                .status(session.getStatus())
                .messageCount(0L)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public ChatResponse sendMessage(Long userId, ChatRequest request) {
        try {
            // 获取或创建会话
            ChatSession session = getOrCreateSession(userId, request.getSessionId());
            
            // 保存用户消息
            ChatMessage userMessage = ChatMessage.createUserMessage(session.getId(), request.getMessage());
            userMessage = chatMessageRepository.save(userMessage);
            
            // 构建上下文消息
            List<Map<String, String>> messages = buildContextMessages(session.getId(), request);
            
            // 调用DeepSeek API
            String aiReply = deepSeekApiClient.sendChatRequestWithRetry(messages);
            
            // 保存AI回复
            ChatMessage aiMessage = ChatMessage.createAssistantMessage(session.getId(), aiReply);
            aiMessage = chatMessageRepository.save(aiMessage);
            
            // 更新会话标题（如果是第一条消息）
            updateSessionTitleIfNeeded(session, request.getMessage());
            
            // 生成建议问题
            List<String> suggestions = generateSuggestions(request.getMessage(), aiReply);
            
            log.info("用户{}在会话{}中发送消息并获得AI回复", userId, session.getSessionId());
            
            return ChatResponse.builder()
                    .sessionId(session.getSessionId())
                    .messageId(aiMessage.getId())
                    .reply(aiReply)
                    .timestamp(aiMessage.getCreatedAt())
                    .suggestions(suggestions)
                    .isNewSession(request.getSessionId() == null)
                    .sessionTitle(session.getTitle())
                    .build();
                    
        } catch (Exception e) {
            log.error("处理聊天消息时发生异常", e);
            throw new BusinessException("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 获取或创建会话
     */
    private ChatSession getOrCreateSession(Long userId, String sessionId) {
        if (sessionId != null) {
            Optional<ChatSession> existingSession = chatSessionRepository.findBySessionId(sessionId);
            if (existingSession.isPresent()) {
                ChatSession session = existingSession.get();
                if (!session.getUserId().equals(userId)) {
                    throw new BusinessException("无权访问该会话");
                }
                return session;
            }
        }
        
        // 创建新会话
        return createNewSession(userId);
    }

    /**
     * 创建新会话实体
     */
    private ChatSession createNewSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setSessionId(IdUtil.simpleUUID());
        session.setUserId(userId);
        session.setTitle("新对话");
        session.setStatus("active");
        return chatSessionRepository.save(session);
    }

    /**
     * 构建上下文消息
     */
    private List<Map<String, String>> buildContextMessages(Long sessionId, ChatRequest request) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统提示
        messages.add(Map.of(
                "role", "system",
                "content", buildSystemPrompt()
        ));
        
        // 如果需要上下文，添加历史消息
        if (request.getUseContext()) {
            List<ChatMessage> contextMessages = chatMessageRepository.findContextMessages(
                    sessionId, 
                    PageRequest.of(0, request.getContextSize())
            );
            
            // 反转顺序，使消息按时间正序排列
            Collections.reverse(contextMessages);
            
            for (ChatMessage msg : contextMessages) {
                messages.add(Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ));
            }
        }
        
        // 添加当前用户消息
        messages.add(Map.of(
                "role", "user",
                "content", request.getMessage()
        ));
        
        return messages;
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt() {
        return "你是一个专业的高考志愿填报助手，具备以下能力：\n" +
               "1. 为高考生提供科学的志愿填报建议\n" +
               "2. 解答关于大学、专业、录取分数线等问题\n" +
               "3. 帮助分析录取概率和推荐合适的院校\n" +
               "4. 提供性格测试和专业匹配建议\n" +
               "5. 解释高考政策和填报规则\n\n" +
               "请用专业、友好、耐心的语气回答用户问题，提供准确、实用的建议。" +
               "如果遇到不确定的信息，请明确告知用户需要进一步核实。";
    }

    /**
     * 更新会话标题（如果需要）
     */
    private void updateSessionTitleIfNeeded(ChatSession session, String userMessage) {
        if ("新对话".equals(session.getTitle())) {
            String newTitle = generateSessionTitle(userMessage);
            session.setTitle(newTitle);
            chatSessionRepository.save(session);
        }
    }

    /**
     * 生成会话标题
     */
    private String generateSessionTitle(String firstMessage) {
        // 简单的标题生成逻辑，可以后续优化
        String title = firstMessage.length() > 20 ? 
                firstMessage.substring(0, 20) + "..." : firstMessage;
        return title.replaceAll("[\\r\\n]+", " ");
    }

    /**
     * 生成建议问题
     */
    private List<String> generateSuggestions(String userMessage, String aiReply) {
        // 基于高考志愿填报场景的常见后续问题
        List<String> suggestions = Arrays.asList(
                "这个专业的就业前景如何？",
                "推荐一些相关的院校",
                "录取分数线大概是多少？",
                "这个专业需要什么样的能力？"
        );

        // 可以根据用户消息和AI回复内容动态生成更相关的建议
        return suggestions.subList(0, Math.min(3, suggestions.size()));
    }

    @Override
    public List<ChatSessionResponse> getUserSessions(Long userId) {
        List<ChatSession> sessions = chatSessionRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(userId, "active");

        return sessions.stream()
                .map(this::convertToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatSessionResponse> getUserSessions(Long userId, Pageable pageable) {
        Page<ChatSession> sessions = chatSessionRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(userId, "active", pageable);

        return sessions.map(this::convertToSessionResponse);
    }

    @Override
    public List<ChatMessage> getChatHistory(String sessionId, Long userId) {
        // 验证会话权限
        validateSessionAccess(sessionId, userId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
    }

    @Override
    public Page<ChatMessage> getChatHistory(String sessionId, Long userId, Pageable pageable) {
        // 验证会话权限
        validateSessionAccess(sessionId, userId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId(), pageable);
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId, Long userId) {
        // 验证会话权限
        validateSessionAccess(sessionId, userId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        // 删除会话消息
        chatMessageRepository.deleteBySessionId(session.getId());

        // 删除会话
        chatSessionRepository.delete(session);

        log.info("用户{}删除了会话{}", userId, sessionId);
    }

    @Override
    @Transactional
    public void updateSessionTitle(String sessionId, Long userId, String title) {
        // 验证会话权限
        validateSessionAccess(sessionId, userId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        session.setTitle(title);
        chatSessionRepository.save(session);

        log.info("用户{}更新了会话{}的标题为: {}", userId, sessionId, title);
    }

    @Override
    @Transactional
    public void archiveSession(String sessionId, Long userId) {
        // 验证会话权限
        validateSessionAccess(sessionId, userId);

        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        session.setStatus("archived");
        chatSessionRepository.save(session);

        log.info("用户{}归档了会话{}", userId, sessionId);
    }

    /**
     * 验证会话访问权限
     */
    private void validateSessionAccess(String sessionId, Long userId) {
        if (!chatSessionRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new BusinessException("无权访问该会话");
        }
    }

    @Override
    public String generateReport(String prompt) {
        try {
            log.info("开始生成测试报告");

            // 构建专门用于报告生成的消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位专业的心理测评师和职业规划师，擅长分析性格测试结果并提供详细的个性化报告。请用专业、温暖、鼓励的语调撰写报告，内容要具体、实用。");
            messages.add(systemMessage);

            // 用户请求
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            // 调用DeepSeek API生成报告
            String report = deepSeekApiClient.sendChatRequestWithRetry(messages);

            log.info("测试报告生成成功，长度: {}", report.length());
            return report;

        } catch (Exception e) {
            log.error("生成测试报告失败", e);
            throw new BusinessException("生成报告失败：" + e.getMessage());
        }
    }

    /**
     * 转换为会话响应DTO
     */
    private ChatSessionResponse convertToSessionResponse(ChatSession session) {
        long messageCount = chatMessageRepository.countBySessionId(session.getId());

        return ChatSessionResponse.builder()
                .sessionId(session.getSessionId())
                .title(session.getTitle())
                .status(session.getStatus())
                .messageCount(messageCount)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
