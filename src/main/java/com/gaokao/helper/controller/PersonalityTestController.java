package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.TestAnswerRequest;
import com.gaokao.helper.dto.response.TestResultResponse;
import com.gaokao.helper.entity.HollandQuestion;
import com.gaokao.helper.entity.MbtiQuestion;
import com.gaokao.helper.service.PersonalityTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 性格测试控制器
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@RestController
@RequestMapping("/api/personality-test")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "性格测试", description = "MBTI和霍兰德职业兴趣测试相关接口")
public class PersonalityTestController {

    private final PersonalityTestService personalityTestService;

    @GetMapping("/mbti/questions")
    @Operation(summary = "获取MBTI测试题目", description = "获取所有MBTI测试题目，按顺序排列")
    public Result<List<MbtiQuestion>> getMbtiQuestions() {
        try {
            List<MbtiQuestion> questions = personalityTestService.getMbtiQuestions();
            return Result.success(questions);
        } catch (Exception e) {
            log.error("获取MBTI测试题目失败", e);
            return Result.error("获取测试题目失败");
        }
    }

    @GetMapping("/holland/questions")
    @Operation(summary = "获取霍兰德测试题目", description = "获取所有霍兰德测试题目，按顺序排列")
    public Result<List<HollandQuestion>> getHollandQuestions() {
        try {
            List<HollandQuestion> questions = personalityTestService.getHollandQuestions();
            return Result.success(questions);
        } catch (Exception e) {
            log.error("获取霍兰德测试题目失败", e);
            return Result.error("获取测试题目失败");
        }
    }

    @PostMapping("/mbti/submit")
    @Operation(summary = "提交MBTI测试答案", description = "提交MBTI测试答案并获取测试结果")
    public Result<TestResultResponse> submitMbtiTest(
            @Valid @RequestBody TestAnswerRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 设置IP地址和用户代理
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            request.setTestType("MBTI");

            TestResultResponse result = personalityTestService.submitMbtiTest(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("提交MBTI测试失败", e);
            return Result.error("提交测试失败");
        }
    }

    @PostMapping("/holland/submit")
    @Operation(summary = "提交霍兰德测试答案", description = "提交霍兰德测试答案并获取测试结果")
    public Result<TestResultResponse> submitHollandTest(
            @Valid @RequestBody TestAnswerRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 设置IP地址和用户代理
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            request.setTestType("HOLLAND");

            TestResultResponse result = personalityTestService.submitHollandTest(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("提交霍兰德测试失败", e);
            return Result.error("提交测试失败");
        }
    }

    @GetMapping("/result/{recordId}")
    @Operation(summary = "获取测试结果", description = "根据测试记录ID获取详细的测试结果")
    public Result<TestResultResponse> getTestResult(
            @Parameter(description = "测试记录ID") @PathVariable Integer recordId) {
        try {
            TestResultResponse result = personalityTestService.getTestResult(recordId);
            if (result != null) {
                return Result.success(result);
            } else {
                return Result.error("测试记录不存在");
            }
        } catch (Exception e) {
            log.error("获取测试结果失败", e);
            return Result.error("获取测试结果失败");
        }
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "获取用户测试历史", description = "获取指定用户的所有测试历史记录")
    public Result<List<TestResultResponse>> getUserTestHistory(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<TestResultResponse> history = personalityTestService.getUserTestHistory(userId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取用户测试历史失败", e);
            return Result.error("获取测试历史失败");
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
