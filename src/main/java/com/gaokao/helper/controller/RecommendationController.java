package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.RecommendationRequest;
import com.gaokao.helper.dto.response.RecommendationResponse;
import com.gaokao.helper.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 学校推荐控制器
 *
 * @author PLeiA
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "学校推荐", description = "提供基于分数的学校推荐功能")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 基础学校推荐 (POST方法)
     */
    @PostMapping("/basic")
    @Operation(summary = "基础学校推荐", description = "根据用户分数、省份、科类等信息推荐合适的学校")
    public Result<RecommendationResponse> getBasicRecommendations(@Valid @RequestBody RecommendationRequest request) {
        try {
            log.info("学校推荐请求: {}", request);
            RecommendationResponse response = recommendationService.getBasicRecommendations(request);
            return Result.success("学校推荐成功", response);
        } catch (Exception e) {
            log.error("学校推荐失败", e);
            return Result.error("学校推荐失败: " + e.getMessage());
        }
    }

    /**
     * 基础学校推荐 (GET方法，支持查询参数)
     */
    @GetMapping("/basic")
    @Operation(summary = "基础学校推荐(GET)", description = "根据查询参数推荐合适的学校")
    public Result<RecommendationResponse> getBasicRecommendationsByParams(
            @RequestParam Integer userScore,
            @RequestParam Integer provinceId,
            @RequestParam Integer subjectCategoryId,
            @RequestParam Integer year,
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            RecommendationRequest request = new RecommendationRequest();
            request.setUserScore(userScore);
            request.setProvinceId(provinceId);
            request.setSubjectCategoryId(subjectCategoryId);
            request.setYear(year);
            request.setLimit(limit);

            log.info("学校推荐请求(GET): {}", request);
            RecommendationResponse response = recommendationService.getBasicRecommendations(request);
            return Result.success("学校推荐成功", response);
        } catch (Exception e) {
            log.error("学校推荐失败", e);
            return Result.error("学校推荐失败: " + e.getMessage());
        }
    }
}
