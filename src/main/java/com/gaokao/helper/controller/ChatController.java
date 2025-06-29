package com.gaokao.helper.controller;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.ChatRequest;
import com.gaokao.helper.dto.response.ChatResponse;
import com.gaokao.helper.dto.response.ChatSessionResponse;
import com.gaokao.helper.entity.ChatMessage;
import com.gaokao.helper.service.ChatService;
import com.gaokao.helper.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * AI对话助手控制器
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "AI对话助手", description = "AI智能对话相关接口")
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    /**
     * 创建新的聊天会话
     */
    @PostMapping("/session")
    @Operation(summary = "创建聊天会话", description = "创建新的AI对话会话")
    public Result<ChatSessionResponse> createSession(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            ChatSessionResponse response = chatService.createSession(userId);
            return Result.success("会话创建成功", response);
        } catch (BusinessException e) {
            log.error("创建会话失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建会话异常", e);
            return Result.error("创建会话失败，请稍后重试");
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    @Operation(summary = "发送消息", description = "向AI助手发送消息并获取回复")
    public Result<ChatResponse> sendMessage(
            @Valid @RequestBody ChatRequest chatRequest,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            ChatResponse response = chatService.sendMessage(userId, chatRequest);
            return Result.success("消息发送成功", response);
        } catch (BusinessException e) {
            log.error("发送消息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("发送消息异常", e);
            return Result.error("发送消息失败，请稍后重试");
        }
    }

    /**
     * 获取用户的聊天会话列表
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取会话列表", description = "获取用户的所有聊天会话")
    public Result<List<ChatSessionResponse>> getUserSessions(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "20") @Parameter(description = "每页大小") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            
            if (page <= 0 || size <= 0) {
                // 不分页，返回所有会话
                List<ChatSessionResponse> sessions = chatService.getUserSessions(userId);
                return Result.success("获取会话列表成功", sessions);
            } else {
                // 分页查询
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<ChatSessionResponse> sessionPage = chatService.getUserSessions(userId, pageable);
                return Result.success("获取会话列表成功", sessionPage.getContent());
            }
        } catch (BusinessException e) {
            log.error("获取会话列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取会话列表异常", e);
            return Result.error("获取会话列表失败，请稍后重试");
        }
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/history/{sessionId}")
    @Operation(summary = "获取聊天历史", description = "获取指定会话的聊天历史记录")
    public Result<List<ChatMessage>> getChatHistory(
            @PathVariable @Parameter(description = "会话ID") String sessionId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "50") @Parameter(description = "每页大小") Integer size,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            
            if (page <= 0 || size <= 0) {
                // 不分页，返回所有历史消息
                List<ChatMessage> messages = chatService.getChatHistory(sessionId, userId);
                return Result.success("获取聊天历史成功", messages);
            } else {
                // 分页查询
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<ChatMessage> messagePage = chatService.getChatHistory(sessionId, userId, pageable);
                return Result.success("获取聊天历史成功", messagePage.getContent());
            }
        } catch (BusinessException e) {
            log.error("获取聊天历史失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取聊天历史异常", e);
            return Result.error("获取聊天历史失败，请稍后重试");
        }
    }

    /**
     * 删除聊天会话
     */
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "删除会话", description = "删除指定的聊天会话及其所有消息")
    public Result<Void> deleteSession(
            @PathVariable @Parameter(description = "会话ID") String sessionId,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            chatService.deleteSession(sessionId, userId);
            return Result.success("会话删除成功");
        } catch (BusinessException e) {
            log.error("删除会话失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除会话异常", e);
            return Result.error("删除会话失败，请稍后重试");
        }
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/session/{sessionId}/title")
    @Operation(summary = "更新会话标题", description = "更新指定会话的标题")
    public Result<Void> updateSessionTitle(
            @PathVariable @Parameter(description = "会话ID") String sessionId,
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            String title = requestBody.get("title");
            
            if (title == null || title.trim().isEmpty()) {
                return Result.error("标题不能为空");
            }
            
            chatService.updateSessionTitle(sessionId, userId, title.trim());
            return Result.success("会话标题更新成功");
        } catch (BusinessException e) {
            log.error("更新会话标题失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新会话标题异常", e);
            return Result.error("更新会话标题失败，请稍后重试");
        }
    }

    /**
     * 归档会话
     */
    @PutMapping("/session/{sessionId}/archive")
    @Operation(summary = "归档会话", description = "将指定会话设置为归档状态")
    public Result<Void> archiveSession(
            @PathVariable @Parameter(description = "会话ID") String sessionId,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            chatService.archiveSession(sessionId, userId);
            return Result.success("会话归档成功");
        } catch (BusinessException e) {
            log.error("归档会话失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("归档会话异常", e);
            return Result.error("归档会话失败，请稍后重试");
        }
    }
}
