package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分数趋势分析响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分数趋势分析响应")
public class ScoreTrendResponse {

    /**
     * 学校信息
     */
    @Schema(description = "学校信息")
    private SchoolInfo schoolInfo;

    /**
     * 趋势数据列表
     */
    @Schema(description = "趋势数据列表")
    private List<TrendData> trendData;

    /**
     * 趋势分析结果
     */
    @Schema(description = "趋势分析结果")
    private TrendAnalysis analysis;

    /**
     * 学校信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学校信息")
    public static class SchoolInfo {
        @Schema(description = "学校ID", example = "1")
        private Integer schoolId;

        @Schema(description = "学校名称", example = "清华大学")
        private String schoolName;

        @Schema(description = "省份名称", example = "河南省")
        private String provinceName;

        @Schema(description = "科类名称", example = "物理组")
        private String subjectCategoryName;
    }

    /**
     * 趋势数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "年份", example = "2024")
        private Integer year;

        @Schema(description = "最低分", example = "650")
        private Integer minScore;

        @Schema(description = "平均分", example = "670")
        private Integer avgScore;

        @Schema(description = "最低位次", example = "1000")
        private Integer minRank;

        @Schema(description = "录取人数", example = "50")
        private Integer admissionCount;

        @Schema(description = "与上年分数差", example = "+5")
        private Integer scoreChange;

        @Schema(description = "与上年位次差", example = "-100")
        private Integer rankChange;
    }

    /**
     * 趋势分析结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "趋势分析结果")
    public static class TrendAnalysis {
        @Schema(description = "总体趋势", example = "上升")
        private String overallTrend;

        @Schema(description = "平均年度变化", example = "+3.2")
        private Double averageYearlyChange;

        @Schema(description = "最大波动", example = "15")
        private Integer maxFluctuation;

        @Schema(description = "稳定性评分", example = "85")
        private Integer stabilityScore;

        @Schema(description = "趋势描述", example = "该校录取分数呈稳步上升趋势，波动较小")
        private String description;

        @Schema(description = "预测建议", example = "建议考生准备比去年高5-10分的成绩")
        private String recommendation;
    }
}
