package com.gaokao.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学校推荐内部处理DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolRecommendation {

    /**
     * 学校ID
     */
    private Integer schoolId;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学校类型
     */
    private String schoolType;

    /**
     * 学校所在省份
     */
    private String schoolProvince;

    /**
     * 历年最低分
     */
    private Integer historicalMinScore;

    /**
     * 历年最低位次
     */
    private Integer historicalMinRank;

    /**
     * 录取概率
     */
    private Double admissionProbability;

    /**
     * 分数差距（用户分数 - 学校最低分）
     */
    private Integer scoreDifference;

    /**
     * 位次差距（用户位次 - 学校最低位次）
     */
    private Integer rankDifference;

    /**
     * 推荐类型：RUSH(冲刺), STABLE(稳妥), SAFE(保底)
     */
    private RecommendationType recommendationType;

    /**
     * 风险等级：HIGH, MEDIUM, LOW
     */
    private RiskLevel riskLevel;

    /**
     * 推荐理由
     */
    private String recommendationReason;

    /**
     * 院校标签, 如 "985,211,双一流", 以逗号分隔
     */
    private String tags;

    /**
     * 办学层次（本科、专科）
     */
    private String schoolLevel;

    /**
     * 办学性质（公办、民办）
     */
    private String ownershipType;

    /**
     * 隶属单位（教育部等）
     */
    private String schoolAffiliation;

    /**
     * 学校简介
     */
    private String schoolIntroduction;

    /**
     * 特色专业
     */
    private String schoolNationalProfessions;

    /**
     * 推荐类型枚举
     */
    public enum RecommendationType {
        RUSH("冲刺"),
        STABLE("稳妥"),
        SAFE("保底");

        private final String description;

        RecommendationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        HIGH("高风险"),
        MEDIUM("中等风险"),
        LOW("低风险");

        private final String description;

        RiskLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
