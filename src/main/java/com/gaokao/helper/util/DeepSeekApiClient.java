package com.gaokao.helper.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.config.DeepSeekConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API客户端
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Component
@Slf4j
public class DeepSeekApiClient {

    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    /**
     * 构造HTTP客户端
     */
    public DeepSeekApiClient(DeepSeekConfig deepSeekConfig, ObjectMapper objectMapper) {
        this.deepSeekConfig = deepSeekConfig;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(deepSeekConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(deepSeekConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(deepSeekConfig.getTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 发送聊天请求到DeepSeek API
     */
    public String sendChatRequest(List<Map<String, String>> messages) throws IOException {
        // 构建请求体
        Map<String, Object> requestBody = Map.of(
                "model", deepSeekConfig.getModel(),
                "messages", messages,
                "max_tokens", deepSeekConfig.getMaxTokens(),
                "temperature", deepSeekConfig.getTemperature(),
                "stream", false
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        log.debug("DeepSeek API请求体: {}", jsonBody);

        // 构建HTTP请求
        Request request = new Request.Builder()
                .url(deepSeekConfig.getBaseUrl() + "/v1/chat/completions")
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求并处理响应
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("DeepSeek API请求失败: HTTP {}, 响应: {}", response.code(), errorBody);
                throw new IOException("DeepSeek API请求失败: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            log.debug("DeepSeek API响应: {}", responseBody);

            // 解析响应
            return parseResponse(responseBody);
        }
    }

    /**
     * 解析DeepSeek API响应
     */
    private String parseResponse(String responseBody) throws IOException {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choices = jsonNode.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            
            log.error("无法解析DeepSeek API响应: {}", responseBody);
            throw new IOException("无法解析DeepSeek API响应");
            
        } catch (Exception e) {
            log.error("解析DeepSeek API响应时发生异常", e);
            throw new IOException("解析响应失败: " + e.getMessage());
        }
    }

    /**
     * 带重试的聊天请求
     */
    public String sendChatRequestWithRetry(List<Map<String, String>> messages) {
        int retries = 0;
        Exception lastException = null;

        while (retries < deepSeekConfig.getMaxRetries()) {
            try {
                return sendChatRequest(messages);
            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("DeepSeek API请求失败，第{}次重试: {}", retries, e.getMessage());
                
                if (retries < deepSeekConfig.getMaxRetries()) {
                    try {
                        Thread.sleep(1000 * retries); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("DeepSeek API请求最终失败，已重试{}次", deepSeekConfig.getMaxRetries(), lastException);
        throw new RuntimeException("AI服务暂时不可用，请稍后重试", lastException);
    }
}
