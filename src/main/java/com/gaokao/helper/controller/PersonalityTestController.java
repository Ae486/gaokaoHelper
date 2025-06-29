package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.request.TestAnswerRequest;
import com.gaokao.helper.dto.response.TestResultResponse;
import com.gaokao.helper.entity.HollandQuestion;
import com.gaokao.helper.entity.MbtiQuestion;
import com.gaokao.helper.service.PersonalityTestService;
import com.gaokao.helper.service.ChatService;
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
    private final ChatService chatService;

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

    @PostMapping("/detailed-report")
    @Operation(summary = "生成详细测试报告", description = "基于测试结果生成AI详细分析报告")
    public Result<String> generateDetailedReport(
            @RequestBody TestResultResponse testResult,
            HttpServletRequest request) {
        try {
            // 构建专门的提示词
            String prompt = buildReportPrompt(testResult);

            // 调用AI服务生成报告
            String report = chatService.generateReport(prompt);

            return Result.success("报告生成成功", report);
        } catch (Exception e) {
            log.error("生成详细报告失败", e);
            return Result.error("生成报告失败，请稍后重试");
        }
    }

    /**
     * 构建生成报告的提示词
     */
    private String buildReportPrompt(TestResultResponse testResult) {
        StringBuilder prompt = new StringBuilder();

        if ("MBTI".equals(testResult.getTestType())) {
            prompt.append("请基于以下MBTI测试结果，生成一份详细的个性化分析报告：\n\n");
            prompt.append("测试类型：").append(testResult.getTestResult()).append(" (").append(testResult.getTypeName()).append(")\n");
            prompt.append("类型描述：").append(testResult.getTypeDescription()).append("\n\n");

            // 添加维度分析
            prompt.append("维度得分分析：\n");
            if (testResult.getDimensionScores() != null) {
                testResult.getDimensionScores().forEach((dimension, score) -> {
                    String dimensionName = getDimensionName(dimension);
                    prompt.append("- ").append(dimensionName).append("：").append(score).append("分\n");
                });
            }

            prompt.append("\n推荐专业：\n");
            if (testResult.getRecommendedMajors() != null && !testResult.getRecommendedMajors().isEmpty()) {
                testResult.getRecommendedMajors().forEach(major -> {
                    prompt.append("- ").append(major.getMajorName())
                          .append(" (匹配度：").append(String.format("%.0f", major.getMatchScore() * 100))
                          .append("%)\n");
                });
            }

            prompt.append("\n请从以下几个方面生成详细报告：\n");
            prompt.append("1. 性格特征深度解析\n");
            prompt.append("2. 优势与潜力分析\n");
            prompt.append("3. 发展建议与注意事项\n");
            prompt.append("4. 职业发展方向建议\n");
            prompt.append("5. 学习方式与环境偏好\n");
            prompt.append("6. 人际交往特点\n");
            prompt.append("7. 专业选择建议与理由\n\n");

        } else if ("HOLLAND".equals(testResult.getTestType())) {
            prompt.append("请基于以下霍兰德职业兴趣测试结果，生成一份详细的个性化分析报告：\n\n");
            prompt.append("职业兴趣类型：").append(testResult.getTestResult()).append("\n");
            prompt.append("类型描述：").append(testResult.getTypeDescription()).append("\n\n");

            // 添加维度分析
            prompt.append("兴趣维度得分：\n");
            if (testResult.getDimensionScores() != null) {
                testResult.getDimensionScores().forEach((dimension, score) -> {
                    String dimensionName = getHollandDimensionName(dimension);
                    prompt.append("- ").append(dimensionName).append("：").append(score).append("分\n");
                });
            }

            prompt.append("\n请从以下几个方面生成详细报告：\n");
            prompt.append("1. 职业兴趣特征分析\n");
            prompt.append("2. 适合的工作环境\n");
            prompt.append("3. 职业发展建议\n");
            prompt.append("4. 专业选择指导\n");
            prompt.append("5. 能力发展方向\n");
            prompt.append("6. 求职与面试建议\n\n");
        }

        prompt.append("请用专业、温暖、鼓励的语调撰写报告，内容要具体、实用，字数控制在1500-2000字左右。");

        return prompt.toString();
    }

    /**
     * 获取MBTI维度名称
     */
    private String getDimensionName(String dimension) {
        switch (dimension) {
            case "EI": return "外向性-内向性";
            case "SN": return "感觉-直觉";
            case "TF": return "思考-情感";
            case "JP": return "判断-感知";
            default: return dimension;
        }
    }

    /**
     * 获取霍兰德维度名称
     */
    private String getHollandDimensionName(String dimension) {
        switch (dimension) {
            case "R": return "现实型 (Realistic)";
            case "I": return "研究型 (Investigative)";
            case "A": return "艺术型 (Artistic)";
            case "S": return "社会型 (Social)";
            case "E": return "企业型 (Enterprising)";
            case "C": return "常规型 (Conventional)";
            default: return dimension;
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
