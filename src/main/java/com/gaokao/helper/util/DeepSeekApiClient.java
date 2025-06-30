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
                .connectTimeout(30, TimeUnit.SECONDS)                    // 连接超时30秒
                .readTimeout(deepSeekConfig.getTimeout(), TimeUnit.MILLISECONDS)  // 读取超时使用配置值
                .writeTimeout(60, TimeUnit.SECONDS)                      // 写入超时60秒
                .callTimeout(deepSeekConfig.getTimeout() + 30000, TimeUnit.MILLISECONDS) // 总超时比读取超时多30秒
                .retryOnConnectionFailure(true)                          // 连接失败自动重试
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
                        // 增加重试间隔：第1次重试等待2秒，第2次等待4秒，第3次等待6秒...
                        long waitTime = 2000 * retries;
                        log.info("等待 {} 毫秒后进行第 {} 次重试", waitTime, retries + 1);
                        Thread.sleep(waitTime);
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

    /**
     * 带重试的聊天请求 - 测试专用版本（更长超时时间）
     */
    public String sendChatRequestWithRetryForTest(List<Map<String, String>> messages) {
        log.info("=== 使用测试专用API客户端，超时时间: 120秒 ===");

        // 创建专用于测试的HTTP客户端，超时时间更长
        OkHttpClient testHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)     // 连接超时30秒
                .readTimeout(120, TimeUnit.SECONDS)       // 读取超时120秒
                .writeTimeout(60, TimeUnit.SECONDS)       // 写入超时60秒
                .callTimeout(150, TimeUnit.SECONDS)       // 总超时150秒
                .retryOnConnectionFailure(true)           // 连接失败自动重试
                .build();

        int retries = 0;
        int maxRetries = 5; // 测试时增加重试次数
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                log.info("测试API请求开始，第{}次尝试", retries + 1);
                long startTime = System.currentTimeMillis();

                // 使用测试专用的HTTP客户端发送请求
                String result = sendChatRequestWithCustomClient(messages, testHttpClient);

                long endTime = System.currentTimeMillis();
                log.info("测试API请求成功，耗时: {}ms", endTime - startTime);

                return result;

            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("测试API请求失败，第{}次重试: {}", retries, e.getMessage());

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(2000 * retries); // 测试时使用更长的重试间隔
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("测试API请求最终失败，已重试{}次", maxRetries, lastException);
        throw new RuntimeException("测试AI服务失败，已尝试" + maxRetries + "次", lastException);
    }

    /**
     * 使用自定义HTTP客户端发送请求
     */
    private String sendChatRequestWithCustomClient(List<Map<String, String>> messages, OkHttpClient customClient) throws IOException {
        // 构建请求体
        Map<String, Object> requestBody = Map.of(
                "model", deepSeekConfig.getModel(),
                "messages", messages,
                "max_tokens", deepSeekConfig.getMaxTokens(),
                "temperature", deepSeekConfig.getTemperature()
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        log.debug("发送请求到DeepSeek API: {}", jsonBody);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(deepSeekConfig.getBaseUrl() + "/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = customClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                log.error("DeepSeek API请求失败: HTTP {}, 响应: {}", response.code(), errorBody);
                throw new IOException("DeepSeek API请求失败: HTTP " + response.code() + ", " + errorBody);
            }

            String responseBody = response.body().string();
            log.debug("DeepSeek API响应: {}", responseBody);

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    String content = message.get("content").asText();
                    log.info("DeepSeek API响应成功，内容长度: {} 字符", content.length());
                    return content;
                }
            }

            throw new IOException("DeepSeek API响应格式异常");
        } catch (Exception e) {
            log.error("解析DeepSeek API响应失败", e);
            throw new IOException("解析响应失败: " + e.getMessage());
        }
    }
}
