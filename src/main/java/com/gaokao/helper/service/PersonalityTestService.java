package com.gaokao.helper.service;

import com.gaokao.helper.dto.request.TestAnswerRequest;
import com.gaokao.helper.dto.response.TestResultResponse;
import com.gaokao.helper.entity.HollandQuestion;
import com.gaokao.helper.entity.MbtiQuestion;

import java.util.List;

/**
 * 性格测试服务接口
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
public interface PersonalityTestService {

    /**
     * 获取MBTI测试题目
     */
    List<MbtiQuestion> getMbtiQuestions();

    /**
     * 获取霍兰德测试题目
     */
    List<HollandQuestion> getHollandQuestions();

    /**
     * 提交MBTI测试答案并计算结果
     */
    TestResultResponse submitMbtiTest(TestAnswerRequest request);

    /**
     * 提交霍兰德测试答案并计算结果
     */
    TestResultResponse submitHollandTest(TestAnswerRequest request);

    /**
     * 根据测试记录ID获取测试结果
     */
    TestResultResponse getTestResult(Integer recordId);

    /**
     * 获取用户的测试历史
     */
    List<TestResultResponse> getUserTestHistory(Integer userId);

    /**
     * 计算MBTI类型
     */
    String calculateMbtiType(List<TestAnswerRequest.Answer> answers);

    /**
     * 计算霍兰德代码
     */
    String calculateHollandCode(List<TestAnswerRequest.Answer> answers);
}
