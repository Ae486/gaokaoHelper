package com.gaokao.helper.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaokao.helper.dto.request.TestAnswerRequest;
import com.gaokao.helper.dto.response.TestResultResponse;
import com.gaokao.helper.entity.*;
import com.gaokao.helper.repository.*;
import com.gaokao.helper.service.PersonalityTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 性格测试服务实现类
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalityTestServiceImpl implements PersonalityTestService {

    private final MbtiQuestionRepository mbtiQuestionRepository;
    private final HollandQuestionRepository hollandQuestionRepository;
    private final MajorRepository majorRepository;
    private final PersonalityTestRecordRepository testRecordRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<MbtiQuestion> getMbtiQuestions() {
        return mbtiQuestionRepository.findAllByOrderByQuestionOrderAsc();
    }

    @Override
    public List<HollandQuestion> getHollandQuestions() {
        return hollandQuestionRepository.findAllByOrderByQuestionOrderAsc();
    }

    @Override
    @Transactional
    public TestResultResponse submitMbtiTest(TestAnswerRequest request) {
        // 1. 计算MBTI类型
        String mbtiType = calculateMbtiType(request.getAnswers());
        
        // 2. 计算维度得分
        Map<String, Integer> dimensionScores = calculateMbtiDimensionScores(request.getAnswers());
        
        // 3. 保存测试记录
        PersonalityTestRecord record = saveTestRecord(request, mbtiType, dimensionScores);
        
        // 4. 构建响应
        return buildMbtiTestResult(record, mbtiType, dimensionScores);
    }

    @Override
    @Transactional
    public TestResultResponse submitHollandTest(TestAnswerRequest request) {
        // 1. 计算霍兰德代码
        String hollandCode = calculateHollandCode(request.getAnswers());
        
        // 2. 计算维度得分
        Map<String, Integer> dimensionScores = calculateHollandDimensionScores(request.getAnswers());
        
        // 3. 保存测试记录
        PersonalityTestRecord record = saveTestRecord(request, hollandCode, dimensionScores);
        
        // 4. 构建响应
        return buildHollandTestResult(record, hollandCode, dimensionScores);
    }

    @Override
    public String calculateMbtiType(List<TestAnswerRequest.Answer> answers) {
        Map<String, Integer> scores = calculateMbtiDimensionScores(answers);
        
        StringBuilder mbtiType = new StringBuilder();
        mbtiType.append(scores.get("EI") > 0 ? "E" : "I");
        mbtiType.append(scores.get("SN") > 0 ? "S" : "N");
        mbtiType.append(scores.get("TF") > 0 ? "T" : "F");
        mbtiType.append(scores.get("JP") > 0 ? "J" : "P");
        
        return mbtiType.toString();
    }

    @Override
    public String calculateHollandCode(List<TestAnswerRequest.Answer> answers) {
        Map<String, Integer> scores = calculateHollandDimensionScores(answers);
        
        // 按得分排序，取前3个维度
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());
    }

    private Map<String, Integer> calculateMbtiDimensionScores(List<TestAnswerRequest.Answer> answers) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("EI", 0);
        scores.put("SN", 0);
        scores.put("TF", 0);
        scores.put("JP", 0);

        List<MbtiQuestion> questions = mbtiQuestionRepository.findAll();
        Map<Integer, MbtiQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(MbtiQuestion::getId, q -> q));

        for (TestAnswerRequest.Answer answer : answers) {
            MbtiQuestion question = questionMap.get(answer.getQuestionId());
            if (question != null) {
                String dimension = question.getDimension().name();
                int score = "A".equals(answer.getAnswer()) ? question.getAScore() : question.getBScore();
                scores.put(dimension, scores.get(dimension) + score);
            }
        }

        return scores;
    }

    private Map<String, Integer> calculateHollandDimensionScores(List<TestAnswerRequest.Answer> answers) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("R", 0);
        scores.put("I", 0);
        scores.put("A", 0);
        scores.put("S", 0);
        scores.put("E", 0);
        scores.put("C", 0);

        List<HollandQuestion> questions = hollandQuestionRepository.findAll();
        Map<Integer, HollandQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(HollandQuestion::getId, q -> q));

        for (TestAnswerRequest.Answer answer : answers) {
            HollandQuestion question = questionMap.get(answer.getQuestionId());
            if (question != null && "YES".equals(answer.getAnswer())) {
                String dimension = question.getDimension().name();
                scores.put(dimension, scores.get(dimension) + 1);
            }
        }

        return scores;
    }

    private PersonalityTestRecord saveTestRecord(TestAnswerRequest request, String result, Map<String, Integer> scores) {
        PersonalityTestRecord record = new PersonalityTestRecord();
        record.setUserId(request.getUserId());
        record.setTestType(PersonalityTestRecord.TestType.valueOf(request.getTestType()));
        record.setTestResult(result);
        record.setTestDuration(request.getTestDuration());
        record.setIpAddress(request.getIpAddress());
        record.setUserAgent(request.getUserAgent());

        try {
            record.setDetailedScores(objectMapper.writeValueAsString(scores));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize scores", e);
        }

        return testRecordRepository.save(record);
    }

    private TestResultResponse buildMbtiTestResult(PersonalityTestRecord record, String mbtiType, Map<String, Integer> scores) {
        TestResultResponse response = new TestResultResponse();
        response.setRecordId(record.getId());
        response.setTestType("MBTI");
        response.setTestResult(mbtiType);
        response.setDimensionScores(scores);
        
        // 计算百分比
        Map<String, Double> percentages = new HashMap<>();
        scores.forEach((key, value) -> {
            double percentage = Math.abs(value) / 15.0 * 100; // 每个维度15题
            percentages.put(key, Math.round(percentage * 100.0) / 100.0);
        });
        response.setDimensionPercentages(percentages);

        // TODO: 添加类型描述和推荐专业
        response.setTypeName("类型名称");
        response.setTypeDescription("类型描述");
        response.setRecommendedMajors(new ArrayList<>());
        response.setPersonalityTraits(new TestResultResponse.PersonalityTraits());

        return response;
    }

    private TestResultResponse buildHollandTestResult(PersonalityTestRecord record, String hollandCode, Map<String, Integer> scores) {
        TestResultResponse response = new TestResultResponse();
        response.setRecordId(record.getId());
        response.setTestType("HOLLAND");
        response.setTestResult(hollandCode);
        response.setDimensionScores(scores);
        
        // 计算百分比
        Map<String, Double> percentages = new HashMap<>();
        scores.forEach((key, value) -> {
            double percentage = value / 10.0 * 100; // 每个维度10题
            percentages.put(key, Math.round(percentage * 100.0) / 100.0);
        });
        response.setDimensionPercentages(percentages);

        // TODO: 添加类型描述和推荐专业
        response.setTypeName("类型名称");
        response.setTypeDescription("类型描述");
        response.setRecommendedMajors(new ArrayList<>());
        response.setPersonalityTraits(new TestResultResponse.PersonalityTraits());

        return response;
    }

    @Override
    public TestResultResponse getTestResult(Integer recordId) {
        // TODO: 实现根据记录ID获取测试结果
        return null;
    }

    @Override
    public List<TestResultResponse> getUserTestHistory(Integer userId) {
        // TODO: 实现获取用户测试历史
        return new ArrayList<>();
    }
}
