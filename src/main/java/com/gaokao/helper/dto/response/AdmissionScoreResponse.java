package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 录取分数查询响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "录取分数查询响应")
public class AdmissionScoreResponse {

    /**
     * 学校信息
     */
    @Schema(description = "学校信息")
    private SchoolInfo schoolInfo;

    /**
     * 录取分数数据列表
     */
    @Schema(description = "录取分数数据列表")
    private List<AdmissionScoreData> admissionScores;

    /**
     * 统计信息
     */
    @Schema(description = "统计信息")
    private Statistics statistics;

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

        @Schema(description = "学校类型", example = "985")
        private String schoolType;

        @Schema(description = "所在省份", example = "北京市")
        private String province;
    }

    /**
     * 录取分数数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "录取分数数据")
    public static class AdmissionScoreData {
        @Schema(description = "年份", example = "2024")
        private Integer year;

        @Schema(description = "省份名称", example = "河南省")
        private String provinceName;

        @Schema(description = "科类名称", example = "物理组")
        private String subjectCategoryName;

        @Schema(description = "批次类型", example = "本科一批")
        private String batchType;

        @Schema(description = "最低分", example = "650")
        private Integer minScore;

        @Schema(description = "最高分", example = "690")
        private Integer maxScore;

        @Schema(description = "平均分", example = "670")
        private Integer avgScore;

        @Schema(description = "最低位次", example = "1000")
        private Integer minRank;

        @Schema(description = "录取人数", example = "50")
        private Integer admissionCount;
    }

    /**
     * 统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "统计信息")
    public static class Statistics {
        @Schema(description = "总记录数", example = "25")
        private Integer totalRecords;

        @Schema(description = "年份范围", example = "2020-2024")
        private String yearRange;

        @Schema(description = "历史最高分", example = "695")
        private Integer historicalMaxScore;

        @Schema(description = "历史最低分", example = "640")
        private Integer historicalMinScore;

        @Schema(description = "平均录取分数", example = "665")
        private Double averageScore;
    }
}
