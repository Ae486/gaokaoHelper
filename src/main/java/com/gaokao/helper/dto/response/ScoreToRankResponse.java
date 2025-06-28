package com.gaokao.helper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分数转位次响应DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分数转位次响应")
public class ScoreToRankResponse {

    /**
     * 输入分数
     */
    @Schema(description = "输入分数", example = "550")
    private Integer inputScore;

    /**
     * 对应位次
     */
    @Schema(description = "对应位次", example = "10000")
    private Integer rank;

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
    public static ScoreToRankResponse exactMatch(Integer inputScore, Integer rank, 
                                               Integer countAtScore, Double percentage, 
                                               Integer totalCount) {
        return new ScoreToRankResponse(inputScore, rank, countAtScore, percentage, 
                                     totalCount, true, "精确匹配");
    }

    /**
     * 创建近似匹配的响应
     */
    public static ScoreToRankResponse approximateMatch(Integer inputScore, Integer rank, 
                                                     Integer countAtScore, Double percentage, 
                                                     Integer totalCount, String description) {
        return new ScoreToRankResponse(inputScore, rank, countAtScore, percentage, 
                                     totalCount, false, description);
    }
}
