package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 位次转分数响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "位次转分数响应")
public class RankToScoreResponse {

    /**
     * 输入位次
     */
    @Schema(description = "输入位次", example = "10000")
    private Integer inputRank;

    /**
     * 对应分数
     */
    @Schema(description = "对应分数", example = "550")
    private Integer score;

    /**
     * 本段人数
     */
    @Schema(description = "本段人数", example = "120")
    private Integer countAtScore;

    /**
     * 累计百分比
     */
    @Schema(description = "累计百分比", example = "15.5")
    private Double percentage;

    /**
     * 总人数
     */
    @Schema(description = "总人数", example = "50000")
    private Integer totalCount;

    /**
     * 是否精确匹配
     */
    @Schema(description = "是否精确匹配", example = "true")
    private Boolean exactMatch;

    /**
     * 匹配说明
     */
    @Schema(description = "匹配说明", example = "精确匹配")
    private String matchDescription;

    /**
     * 创建精确匹配的响应
     */
    public static RankToScoreResponse exactMatch(Integer inputRank, Integer score, 
                                               Integer countAtScore, Double percentage, 
                                               Integer totalCount) {
        return new RankToScoreResponse(inputRank, score, countAtScore, percentage, 
                                     totalCount, true, "精确匹配");
    }

    /**
     * 创建近似匹配的响应
     */
    public static RankToScoreResponse approximateMatch(Integer inputRank, Integer score, 
                                                     Integer countAtScore, Double percentage, 
                                                     Integer totalCount, String description) {
        return new RankToScoreResponse(inputRank, score, countAtScore, percentage, 
                                     totalCount, false, description);
    }
}
