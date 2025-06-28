package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 一分一段查询响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "一分一段查询响应")
public class ScoreRankingResponse {

    /**
     * 查询条件信息
     */
    @Schema(description = "查询条件信息")
    private QueryInfo queryInfo;

    /**
     * 分数分布数据
     */
    @Schema(description = "分数分布数据")
    private List<ScoreDistribution> scoreDistributions;

    /**
     * 统计信息
     */
    @Schema(description = "统计信息")
    private Statistics statistics;

    /**
     * 查询条件信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "查询条件信息")
    public static class QueryInfo {
        @Schema(description = "年份", example = "2024")
        private Integer year;

        @Schema(description = "省份ID", example = "1")
        private Integer provinceId;

        @Schema(description = "省份名称", example = "安徽省")
        private String provinceName;

        @Schema(description = "科类ID", example = "1")
        private Integer subjectCategoryId;

        @Schema(description = "科类名称", example = "物理组")
        private String subjectCategoryName;
    }

    /**
     * 分数分布数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分数分布数据")
    public static class ScoreDistribution {
        @Schema(description = "分数", example = "550")
        private Integer score;

        @Schema(description = "本段人数", example = "120")
        private Integer countAtScore;

        @Schema(description = "累计人数（位次）", example = "10000")
        private Integer cumulativeCount;

        @Schema(description = "累计百分比", example = "15.5")
        private Double percentage;
    }

    /**
     * 统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "统计信息")
    public static class Statistics {
        @Schema(description = "最高分", example = "680")
        private Integer maxScore;

        @Schema(description = "最低分", example = "200")
        private Integer minScore;

        @Schema(description = "总人数", example = "50000")
        private Integer totalCount;

        @Schema(description = "数据记录数", example = "481")
        private Integer recordCount;
    }
}
