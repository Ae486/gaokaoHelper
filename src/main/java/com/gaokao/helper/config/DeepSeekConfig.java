package com.gaokao.helper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API配置类
 *
 * @author PLeiA
 * @since 2024-06-29
 */
@Configuration
@ConfigurationProperties(prefix = "gaokao.helper.ai.deepseek")
@Data
public class DeepSeekConfig {

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * 使用的模型
     */
    private String model = "deepseek-chat";

    /**
     * 最大token数
     */
    private Integer maxTokens = 2000;

    /**
     * 温度参数（0-2）
     */
    private Double temperature = 0.7;

    /**
     * 请求超时时间（毫秒）
     */
    private Long timeout = 30000L;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;
}
