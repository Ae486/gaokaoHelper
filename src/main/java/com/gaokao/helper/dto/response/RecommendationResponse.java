package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学校推荐响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学校推荐响应")
public class RecommendationResponse {

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 冲刺学校列表
     */
    @Schema(description = "冲刺学校列表")
    private List<SchoolRecommendation> rushSchools;

    /**
     * 稳妥学校列表
     */
    @Schema(description = "稳妥学校列表")
    private List<SchoolRecommendation> stableSchools;

    /**
     * 保底学校列表
     */
    @Schema(description = "保底学校列表")
    private List<SchoolRecommendation> safeSchools;

    /**
     * 推荐统计信息
     */
    @Schema(description = "推荐统计信息")
    private RecommendationStatistics statistics;

    /**
     * 用户信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户分数", example = "580")
        private Integer userScore;

        @Schema(description = "省份名称", example = "河南省")
        private String provinceName;

        @Schema(description = "科类名称", example = "物理组")
        private String subjectCategoryName;

        @Schema(description = "年份", example = "2024")
        private Integer year;

        @Schema(description = "用户位次（估算）", example = "25000")
        private Integer estimatedRank;
    }

    /**
     * 学校推荐信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学校推荐信息")
    public static class SchoolRecommendation {
        @Schema(description = "学校ID", example = "1")
        private Integer schoolId;

        @Schema(description = "学校名称", example = "清华大学")
        private String schoolName;

        @Schema(description = "学校类型", example = "综合")
        private String schoolType;

        @Schema(description = "所在省份", example = "北京市")
        private String schoolProvince;

        @Schema(description = "历年最低分", example = "650")
        private Integer historicalMinScore;

        @Schema(description = "历年最低位次", example = "1000")
        private Integer historicalMinRank;

        @Schema(description = "录取概率", example = "75.5")
        private Double admissionProbability;

        @Schema(description = "分数差距", example = "-70")
        private Integer scoreDifference;

        @Schema(description = "位次差距", example = "-24000")
        private Integer rankDifference;

        @Schema(description = "推荐理由", example = "该校历年录取分数稳定，您的分数有较大概率被录取")
        private String recommendationReason;

        @Schema(description = "风险等级", example = "LOW")
        private String riskLevel;

        @Schema(description = "院校标签", example = "985,211,双一流")
        private String tags;

        @Schema(description = "办学层次", example = "本科")
        private String schoolLevel;

        @Schema(description = "办学性质", example = "公办")
        private String ownershipType;

        @Schema(description = "隶属单位", example = "教育部")
        private String schoolAffiliation;

        @Schema(description = "学校简介")
        private String schoolIntroduction;

        @Schema(description = "特色专业")
        private String schoolNationalProfessions;

        @Schema(description = "省份ID", example = "20")
        private Integer provinceId;
    }

    /**
     * 推荐统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "推荐统计信息")
    public static class RecommendationStatistics {
        @Schema(description = "冲刺学校数量", example = "8")
        private Integer rushCount;

        @Schema(description = "稳妥学校数量", example = "12")
        private Integer stableCount;

        @Schema(description = "保底学校数量", example = "10")
        private Integer safeCount;

        @Schema(description = "总推荐学校数量", example = "30")
        private Integer totalCount;

        @Schema(description = "平均录取概率", example = "68.5")
        private Double averageProbability;

        @Schema(description = "推荐建议", example = "建议重点关注稳妥类学校，适当冲刺高分学校")
        private String recommendation;
    }
}
