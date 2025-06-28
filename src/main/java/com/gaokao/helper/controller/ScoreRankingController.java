package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.RankToScoreRequest;
import com.gaokao.helper.dto.request.ScoreRankingRequest;
import com.gaokao.helper.dto.request.ScoreToRankRequest;
import com.gaokao.helper.dto.response.RankToScoreResponse;
import com.gaokao.helper.dto.response.ScoreRankingResponse;
import com.gaokao.helper.dto.response.ScoreToRankResponse;
import com.gaokao.helper.service.ScoreRankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 一分一段查询控制器
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/api/score-ranking")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "一分一段查询", description = "提供分数分布查询、分数位次转换等功能")
public class ScoreRankingController {

    private final ScoreRankingService scoreRankingService;

    /**
     * 查询分数分布数据
     */
    @PostMapping("/distribution")
    @Operation(summary = "查询分数分布数据", description = "根据年份、省份、科类查询分数分布数据，支持分数范围筛选")
    public Result<ScoreRankingResponse> queryScoreDistribution(@Valid @RequestBody ScoreRankingRequest request) {
        try {
            log.info("查询分数分布数据请求: {}", request);
            ScoreRankingResponse response = scoreRankingService.queryScoreDistribution(request);
            return Result.success("查询分数分布数据成功", response);
        } catch (Exception e) {
            log.error("查询分数分布数据失败", e);
            return Result.error("查询分数分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 分数转位次
     */
    @PostMapping("/score-to-rank")
    @Operation(summary = "分数转位次", description = "输入分数，返回对应的位次信息")
    public Result<ScoreToRankResponse> convertScoreToRank(@Valid @RequestBody ScoreToRankRequest request) {
        try {
            log.info("分数转位次请求: {}", request);
            ScoreToRankResponse response = scoreRankingService.convertScoreToRank(request);
            return Result.success("分数转位次成功", response);
        } catch (Exception e) {
            log.error("分数转位次失败", e);
            return Result.error("分数转位次失败: " + e.getMessage());
        }
    }

    /**
     * 位次转分数
     */
    @PostMapping("/rank-to-score")
    @Operation(summary = "位次转分数", description = "输入位次，返回对应的分数信息")
    public Result<RankToScoreResponse> convertRankToScore(@Valid @RequestBody RankToScoreRequest request) {
        try {
            log.info("位次转分数请求: {}", request);
            RankToScoreResponse response = scoreRankingService.convertRankToScore(request);
            return Result.success("位次转分数成功", response);
        } catch (Exception e) {
            log.error("位次转分数失败", e);
            return Result.error("位次转分数失败: " + e.getMessage());
        }
    }

    /**
     * 获取分数统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取分数统计信息", description = "获取指定条件下的分数统计信息，包括最高分、最低分、总人数等")
    public Result<ScoreRankingResponse.Statistics> getScoreStatistics(
            @Parameter(description = "年份") @RequestParam @NotNull @Min(2020) @Max(2030) Integer year,
            @Parameter(description = "省份ID") @RequestParam @NotNull @Min(1) Integer provinceId,
            @Parameter(description = "科类ID") @RequestParam @NotNull @Min(1) Integer subjectCategoryId) {
        try {
            log.info("获取分数统计信息请求: year={}, provinceId={}, subjectCategoryId={}", 
                    year, provinceId, subjectCategoryId);
            ScoreRankingResponse.Statistics statistics = scoreRankingService.getScoreStatistics(
                    year, provinceId, subjectCategoryId);
            return Result.success("获取分数统计信息成功", statistics);
        } catch (Exception e) {
            log.error("获取分数统计信息失败", e);
            return Result.error("获取分数统计信息失败: " + e.getMessage());
        }
    }
}
