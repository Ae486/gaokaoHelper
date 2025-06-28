package com.gaokao.helper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 学校推荐请求DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学校推荐请求")
public class RecommendationRequest {

    /**
     * 用户分数
     */
    @NotNull(message = "用户分数不能为空")
    @Min(value = 0, message = "用户分数不能小于0")
    @Max(value = 750, message = "用户分数不能大于750")
    @Schema(description = "用户分数", example = "580")
    private Integer userScore;

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
     * 年份（可选，默认使用最新年份）
     */
    @Min(value = 2020, message = "年份不能小于2020")
    @Max(value = 2030, message = "年份不能大于2030")
    @Schema(description = "年份", example = "2024")
    private Integer year = 2024;

    /**
     * 推荐学校数量限制（可选，默认30所）
     */
    @Min(value = 5, message = "推荐学校数量不能小于5")
    @Max(value = 100, message = "推荐学校数量不能大于100")
    @Schema(description = "推荐学校数量限制", example = "30")
    private Integer limit = 30;

    /**
     * 学校类型筛选（可选）
     */
    @Schema(description = "学校类型筛选", example = "985")
    private String schoolType;

    /**
     * 是否只推荐公办学校（可选）
     */
    @Schema(description = "是否只推荐公办学校", example = "true")
    private Boolean publicOnly = false;
}
