package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.dto.response.TestResultResponse;
import com.gaokao.helper.service.ChatService;
import com.gaokao.helper.service.PersonalityTestService;
import com.gaokao.helper.util.DeepSeekApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 测试报告功能验证控制器
 * 
 * @author PLeiA
 * @since 2024-06-29
 */
@RestController
@RequestMapping("/api/test-report")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "测试报告验证", description = "用于验证报告生成功能的测试接口")
public class TestReportController {

    private final PersonalityTestService personalityTestService;
    private final ChatService chatService;
    private final DeepSeekApiClient deepSeekApiClient;
    private final ObjectMapper objectMapper;

    /**
     * 步骤1：获取模拟的MBTI测试结果
     */
    @GetMapping("/step1/mock-mbti-result")
    @Operation(summary = "步骤1：获取模拟MBTI测试结果", description = "创建一个模拟的MBTI测试结果用于测试")
    public Result<TestResultResponse> getMockMbtiResult() {
        try {
            log.info("=== 步骤1：创建模拟MBTI测试结果 ===");
            
            TestResultResponse mockResult = createMockMbtiResult();
            
            log.info("模拟MBTI测试结果创建成功:");
            log.info("- 测试类型: {}", mockResult.getTestType());
            log.info("- 测试结果: {}", mockResult.getTestResult());
            log.info("- 类型名称: {}", mockResult.getTypeName());
            log.info("- 维度得分: {}", mockResult.getDimensionScores());
            log.info("- 推荐专业数量: {}", mockResult.getRecommendedMajors() != null ? mockResult.getRecommendedMajors().size() : 0);
            
            return Result.success("模拟MBTI测试结果获取成功", mockResult);
        } catch (Exception e) {
            log.error("获取模拟MBTI测试结果失败", e);
            return Result.error("获取模拟测试结果失败: " + e.getMessage());
        }
    }

    /**
     * 步骤2：构建提示词
     */
    @PostMapping("/step2/build-prompt")
    @Operation(summary = "步骤2：构建AI提示词", description = "根据测试结果构建发送给AI的提示词")
    public Result<String> buildPrompt(@RequestBody TestResultResponse testResult) {
        try {
            log.info("=== 步骤2：构建AI提示词 ===");
            log.info("接收到的原始请求体信息:");
            log.info("- 测试类型: {}", testResult.getTestType());
            log.info("- 测试结果: {}", testResult.getTestResult());
            log.info("- 类型名称: {}", testResult.getTypeName());
            log.info("- 类型描述: {}", testResult.getTypeDescription());
            log.info("- 维度得分: {}", testResult.getDimensionScores());
            log.info("- 推荐专业: {}", testResult.getRecommendedMajors());

            String prompt = buildReportPrompt(testResult);

            log.info("提示词构建成功，长度: {} 字符", prompt.length());
            log.info("提示词内容预览: {}", prompt.substring(0, Math.min(200, prompt.length())) + "...");

            return Result.success("提示词构建成功", prompt);
        } catch (Exception e) {
            log.error("构建提示词失败，详细错误信息:", e);
            log.error("错误类型: {}", e.getClass().getSimpleName());
            log.error("错误消息: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            return Result.error("构建提示词失败: " + e.getMessage());
        }
    }

    /**
     * 步骤3：测试DeepSeek API连接
     */
    @PostMapping("/step3/test-deepseek-connection")
    @Operation(summary = "步骤3：测试DeepSeek API连接", description = "发送简单消息测试DeepSeek API连接")
    public Result<String> testDeepSeekConnection() {
        try {
            log.info("=== 步骤3：测试DeepSeek API连接 ===");
            
            // 构建简单的测试消息
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个测试助手，请简短回复。");
            messages.add(systemMessage);
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "请回复'连接测试成功'来确认API正常工作。");
            messages.add(userMessage);
            
            log.info("发送测试消息到DeepSeek API...");
            String response = deepSeekApiClient.sendChatRequestWithRetry(messages);
            
            log.info("DeepSeek API响应成功，响应长度: {} 字符", response.length());
            log.info("API响应内容: {}", response);
            
            return Result.success("DeepSeek API连接测试成功", response);
        } catch (Exception e) {
            log.error("DeepSeek API连接测试失败", e);
            return Result.error("DeepSeek API连接失败: " + e.getMessage());
        }
    }

    /**
     * 调试接口：测试JSON解析
     */
    @PostMapping("/debug/test-json-parsing")
    @Operation(summary = "调试：测试JSON解析", description = "测试JSON数据是否能正确解析为TestResultResponse对象")
    public Result<String> testJsonParsing(@RequestBody String rawJson) {
        try {
            log.info("=== 调试：测试JSON解析 ===");
            log.info("接收到的原始JSON字符串:");
            log.info("{}", rawJson);
            log.info("JSON字符串长度: {} 字符", rawJson.length());

            // 尝试手动解析JSON
            TestResultResponse testResult = objectMapper.readValue(rawJson, TestResultResponse.class);

            log.info("JSON解析成功！解析结果:");
            log.info("- 测试类型: {}", testResult.getTestType());
            log.info("- 测试结果: {}", testResult.getTestResult());
            log.info("- 类型名称: {}", testResult.getTypeName());
            log.info("- 类型描述: {}", testResult.getTypeDescription());
            log.info("- 维度得分: {}", testResult.getDimensionScores());
            log.info("- 推荐专业数量: {}", testResult.getRecommendedMajors() != null ? testResult.getRecommendedMajors().size() : 0);

            return Result.success("JSON解析成功", "解析的对象类型: " + testResult.getTestResult());
        } catch (Exception e) {
            log.error("JSON解析失败，详细错误信息:", e);
            log.error("错误类型: {}", e.getClass().getSimpleName());
            log.error("错误消息: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            return Result.error("JSON解析失败: " + e.getMessage());
        }
    }

    /**
     * 步骤4：完整的报告生成测试
     */
    @PostMapping("/step4/generate-full-report")
    @Operation(summary = "步骤4：生成完整报告", description = "使用模拟数据生成完整的AI报告")
    public Result<String> generateFullReport(@RequestBody TestResultResponse testResult) {
        try {
            log.info("=== 步骤4：生成完整AI报告 ===");
            log.info("接收到的测试结果详细信息:");
            log.info("- 测试类型: {}", testResult.getTestType());
            log.info("- 测试结果: {}", testResult.getTestResult());
            log.info("- 类型名称: {}", testResult.getTypeName());
            log.info("- 类型描述: {}", testResult.getTypeDescription());

            // 构建提示词
            log.info("开始构建提示词...");
            String prompt = buildReportPrompt(testResult);
            log.info("提示词构建完成，长度: {} 字符", prompt.length());
            log.info("提示词完整内容:\n{}", prompt);

            // 调用ChatService生成报告
            log.info("开始调用ChatService生成报告...");
            String report = chatService.generateReport(prompt);

            log.info("AI报告生成成功，报告长度: {} 字符", report.length());
            log.info("报告完整内容:\n{}", report);

            return Result.success("AI报告生成成功", report);
        } catch (Exception e) {
            log.error("生成AI报告失败，详细错误信息:", e);
            log.error("错误类型: {}", e.getClass().getSimpleName());
            log.error("错误消息: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }

            // 打印完整的堆栈跟踪
            log.error("完整堆栈跟踪:", e);

            return Result.error("生成AI报告失败: " + e.getMessage());
        }
    }

    /**
     * 创建模拟的MBTI测试结果
     */
    private TestResultResponse createMockMbtiResult() {
        TestResultResponse result = new TestResultResponse();
        result.setTestType("MBTI");
        result.setTestResult("INTJ");
        result.setTypeName("建筑师");
        result.setTypeDescription("独立、果断、有想象力和战略思维的完美主义者");
        
        // 设置维度得分
        Map<String, Integer> dimensionScores = new HashMap<>();
        dimensionScores.put("EI", -5);  // 内向
        dimensionScores.put("SN", 3);   // 直觉
        dimensionScores.put("TF", 7);   // 思考
        dimensionScores.put("JP", 4);   // 判断
        result.setDimensionScores(dimensionScores);
        
        // 设置维度百分比
        Map<String, Double> dimensionPercentages = new HashMap<>();
        dimensionPercentages.put("EI", 33.33);
        dimensionPercentages.put("SN", 20.0);
        dimensionPercentages.put("TF", 46.67);
        dimensionPercentages.put("JP", 26.67);
        result.setDimensionPercentages(dimensionPercentages);
        
        // 设置推荐专业
        List<TestResultResponse.RecommendedMajor> majors = new ArrayList<>();
        
        TestResultResponse.RecommendedMajor major1 = new TestResultResponse.RecommendedMajor();
        major1.setMajorName("计算机科学与技术");
        major1.setCategory("工学");
        major1.setMatchScore(0.95);
        major1.setReason("具有强烈的逻辑思维和系统性思考能力，适合复杂的技术问题解决");
        majors.add(major1);
        
        TestResultResponse.RecommendedMajor major2 = new TestResultResponse.RecommendedMajor();
        major2.setMajorName("数学与应用数学");
        major2.setCategory("理学");
        major2.setMatchScore(0.90);
        major2.setReason("具有强大的抽象思维和理论分析能力");
        majors.add(major2);
        
        result.setRecommendedMajors(majors);
        
        return result;
    }

    /**
     * 构建报告提示词（复制自PersonalityTestController）
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
}
