package com.gaokao.helper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 录取分数查询请求DTO
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "录取分数查询请求")
public class AdmissionScoreRequest {

    /**
     * 学校ID
     */
    @NotNull(message = "学校ID不能为空")
    @Min(value = 1, message = "学校ID必须大于0")
    @Schema(description = "学校ID", example = "1")
    private Integer schoolId;

    /**
     * 省份ID（可选）
     */
    @Min(value = 1, message = "省份ID必须大于0")
    @Schema(description = "省份ID", example = "1")
    private Integer provinceId;

    /**
     * 科类ID（可选）
     */
    @Min(value = 1, message = "科类ID必须大于0")
    @Schema(description = "科类ID", example = "1")
    private Integer subjectCategoryId;

    /**
     * 开始年份（可选）
     */
    @Min(value = 2020, message = "开始年份不能小于2020")
    @Max(value = 2030, message = "开始年份不能大于2030")
    @Schema(description = "开始年份", example = "2020")
    private Integer startYear;

    /**
     * 结束年份（可选）
     */
    @Min(value = 2020, message = "结束年份不能小于2020")
    @Max(value = 2030, message = "结束年份不能大于2030")
    @Schema(description = "结束年份", example = "2024")
    private Integer endYear;

    /**
     * 批次类型（可选）
     */
    @Schema(description = "批次类型", example = "本科一批")
    private String batchType;
}
