package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.AdmissionScoreRequest;
import com.gaokao.helper.dto.request.ScoreTrendRequest;
import com.gaokao.helper.dto.response.AdmissionScoreResponse;
import com.gaokao.helper.dto.response.ScoreTrendResponse;
import com.gaokao.helper.service.AdmissionScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 录取分数查询控制器
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/api/admission-score")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "录取分数查询", description = "提供学校历年录取分数查询、趋势分析等功能")
public class AdmissionScoreController {

    private final AdmissionScoreService admissionScoreService;

    /**
     * 按学校查询历年录取分数
     */
    @PostMapping("/historical")
    @Operation(summary = "按学校查询历年录取分数", description = "根据学校ID查询该学校在各省份、各科类的历年录取分数数据")
    public Result<AdmissionScoreResponse> getHistoricalScoresBySchool(@Valid @RequestBody AdmissionScoreRequest request) {
        try {
            log.info("查询学校历年录取分数请求: {}", request);
            AdmissionScoreResponse response = admissionScoreService.getHistoricalScoresBySchool(request);
            return Result.success("查询学校历年录取分数成功", response);
        } catch (Exception e) {
            log.error("查询学校历年录取分数失败", e);
            return Result.error("查询学校历年录取分数失败: " + e.getMessage());
        }
    }

    /**
     * 按专业查询录取分数
     */
    @PostMapping("/by-major")
    @Operation(summary = "按专业查询录取分数", description = "按专业查询录取分数（当前按学校实现）")
    public Result<AdmissionScoreResponse> getScoresByMajor(@Valid @RequestBody AdmissionScoreRequest request) {
        try {
            log.info("按专业查询录取分数请求: {}", request);
            AdmissionScoreResponse response = admissionScoreService.getScoresByMajor(request);
            return Result.success("按专业查询录取分数成功", response);
        } catch (Exception e) {
            log.error("按专业查询录取分数失败", e);
            return Result.error("按专业查询录取分数失败: " + e.getMessage());
        }
    }

    /**
     * 录取分数趋势分析
     */
    @PostMapping("/trend-analysis")
    @Operation(summary = "录取分数趋势分析", description = "分析指定学校在特定省份和科类的录取分数变化趋势")
    public Result<ScoreTrendResponse> analyzeScoreTrend(@Valid @RequestBody ScoreTrendRequest request) {
        try {
            log.info("录取分数趋势分析请求: {}", request);
            ScoreTrendResponse response = admissionScoreService.analyzeScoreTrend(request);
            return Result.success("录取分数趋势分析成功", response);
        } catch (Exception e) {
            log.error("录取分数趋势分析失败", e);
            return Result.error("录取分数趋势分析失败: " + e.getMessage());
        }
    }
}
