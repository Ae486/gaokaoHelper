package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 测试结果响应
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试结果响应")
public class TestResultResponse {

    @Schema(description = "测试记录ID", example = "1")
    private Integer recordId;

    @Schema(description = "测试类型", example = "MBTI")
    private String testType;

    @Schema(description = "测试结果", example = "INTJ")
    private String testResult;

    @Schema(description = "类型名称", example = "建筑师")
    private String typeName;

    @Schema(description = "类型描述")
    private String typeDescription;

    @Schema(description = "维度得分")
    private Map<String, Integer> dimensionScores;

    @Schema(description = "维度百分比")
    private Map<String, Double> dimensionPercentages;

    @Schema(description = "推荐专业列表")
    private List<RecommendedMajor> recommendedMajors;

    @Schema(description = "性格特征")
    private PersonalityTraits personalityTraits;

    /**
     * 推荐专业
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "推荐专业")
    public static class RecommendedMajor {
        
        @Schema(description = "专业ID", example = "1")
        private Integer majorId;

        @Schema(description = "专业名称", example = "计算机科学与技术")
        private String majorName;

        @Schema(description = "专业类别", example = "工学")
        private String category;

        @Schema(description = "匹配度", example = "0.95")
        private Double matchScore;

        @Schema(description = "推荐理由")
        private String reason;

        @Schema(description = "就业前景")
        private String careerProspects;
    }

    /**
     * 性格特征
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "性格特征")
    public static class PersonalityTraits {
        
        @Schema(description = "优势特点")
        private String strengths;

        @Schema(description = "发展建议")
        private String weaknesses;

        @Schema(description = "著名代表人物")
        private String famousPeople;

        @Schema(description = "适合的职业")
        private String suitableCareers;

        @Schema(description = "特征描述")
        private String characteristics;

        @Schema(description = "适合的工作环境")
        private String suitableEnvironments;

        @Schema(description = "典型职业")
        private String typicalOccupations;
    }
}
