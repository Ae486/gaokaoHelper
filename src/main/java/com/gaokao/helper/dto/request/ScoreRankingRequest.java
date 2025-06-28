package com.gaokao.helper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 一分一段查询请求DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "一分一段查询请求")
public class ScoreRankingRequest {

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空")
    @Min(value = 2020, message = "年份不能小于2020")
    @Max(value = 2030, message = "年份不能大于2030")
    @Schema(description = "年份", example = "2024")
    private Integer year;

    /**
     * 省份ID
     */
    @NotNull(message = "省份ID不能为空")
    @Min(value = 1, message = "省份ID必须大于0")
    @Schema(description = "省份ID", example = "1")
    private Integer provinceId;

    /**
     * 科类ID
     */
    @NotNull(message = "科类ID不能为空")
    @Min(value = 1, message = "科类ID必须大于0")
    @Schema(description = "科类ID", example = "1")
    private Integer subjectCategoryId;

    /**
     * 最低分数（可选）
     */
    @Min(value = 0, message = "最低分数不能小于0")
    @Max(value = 750, message = "最低分数不能大于750")
    @Schema(description = "最低分数", example = "400")
    private Integer minScore;

    /**
     * 最高分数（可选）
     */
    @Min(value = 0, message = "最高分数不能小于0")
    @Max(value = 750, message = "最高分数不能大于750")
    @Schema(description = "最高分数", example = "600")
    private Integer maxScore;
}
