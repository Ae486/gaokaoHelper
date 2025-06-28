package com.gaokao.helper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 测试答案提交请求
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试答案提交请求")
public class TestAnswerRequest {

    @Schema(description = "测试类型", example = "MBTI", allowableValues = {"MBTI", "HOLLAND"})
    @NotNull(message = "测试类型不能为空")
    private String testType;

    @Schema(description = "用户ID（可选，匿名测试时为空）", example = "1")
    private Integer userId;

    @Schema(description = "答案列表")
    @NotEmpty(message = "答案列表不能为空")
    @Valid
    private List<Answer> answers;

    @Schema(description = "测试时长（秒）", example = "300")
    private Integer testDuration;

    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;

    /**
     * 单个答案
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单个答案")
    public static class Answer {
        
        @Schema(description = "题目ID", example = "1")
        @NotNull(message = "题目ID不能为空")
        private Integer questionId;

        @Schema(description = "答案", example = "A")
        @NotNull(message = "答案不能为空")
        private String answer;
    }
}
