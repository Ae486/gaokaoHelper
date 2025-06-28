package com.gaokao.helper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分数趋势分析请求DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分数趋势分析请求")
public class ScoreTrendRequest {

    /**
     * 学校ID
     */
    @NotNull(message = "学校ID不能为空")
    @Min(value = 1, message = "学校ID必须大于0")
    @Schema(description = "学校ID", example = "1")
    private Integer schoolId;

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
     * 分析年份数（可选，默认5年）
     */
    @Min(value = 2, message = "分析年份数不能小于2")
    @Max(value = 10, message = "分析年份数不能大于10")
    @Schema(description = "分析年份数", example = "5")
    private Integer yearCount = 5;
}
