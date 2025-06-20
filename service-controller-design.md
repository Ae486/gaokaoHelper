# Service业务逻辑层与Controller控制器层设计

## 1. 核心Service接口设计

### 1.1 PredictionService (录取概率预测服务)
```java
public interface PredictionService {
    
    /**
     * 计算录取概率
     */
    PredictionResponse calculatePrediction(PredictionRequest request);
    
    /**
     * 批量预测
     */
    List<PredictionResponse> batchPredict(BatchPredictionRequest request);
    
    /**
     * 获取用户预测历史
     */
    List<PredictionRecord> getUserPredictionHistory(Integer userId, Pageable pageable);
    
    /**
     * 获取院校预测统计
     */
    UniversityPredictionStats getUniversityPredictionStats(Integer universityId);
}

@Service
@Transactional
@Slf4j
public class PredictionServiceImpl implements PredictionService {
    
    @Autowired
    private AdmissionScoreRepository admissionScoreRepository;
    
    @Autowired
    private ScoreRankingRepository scoreRankingRepository;
    
    @Autowired
    private PredictionRecordRepository predictionRecordRepository;
    
    @Autowired
    private PredictionAlgorithm predictionAlgorithm;
    
    @Override
    public PredictionResponse calculatePrediction(PredictionRequest request) {
        // 1. 参数验证
        validatePredictionRequest(request);
        
        // 2. 获取历史录取数据
        List<AdmissionScore> historicalScores = admissionScoreRepository
            .findHistoricalScores(
                request.getUniversityId(),
                request.getProvinceId(),
                request.getSubjectTypeId(),
                Arrays.asList(2021, 2022, 2023, 2024)
            );
        
        // 3. 计算用户位次
        Integer userRank = calculateUserRank(request);
        
        // 4. 应用预测算法
        PredictionResult result = predictionAlgorithm.predict(
            historicalScores, userRank, request.getUserScore()
        );
        
        // 5. 保存预测记录
        savePredictionRecord(request, result);
        
        // 6. 构建响应
        return buildPredictionResponse(result, userRank, historicalScores);
    }
    
    private Integer calculateUserRank(PredictionRequest request) {
        Optional<ScoreRanking> ranking = scoreRankingRepository
            .findByExactScore(
                request.getYear(),
                request.getProvinceId(),
                request.getSubjectTypeId(),
                request.getUserScore()
            );
        
        return ranking.map(ScoreRanking::getCumulativeCount)
                     .orElse(estimateRankByScore(request));
    }
    
    private void savePredictionRecord(PredictionRequest request, PredictionResult result) {
        PredictionRecord record = new PredictionRecord();
        record.setUserId(request.getUserId());
        record.setUniversityId(request.getUniversityId());
        record.setMajorId(request.getMajorId());
        record.setPredictionProbability(result.getProbability());
        record.setPredictionLevel(result.getLevel());
        record.setPredictionDetails(result.getDetailsJson());
        
        predictionRecordRepository.save(record);
    }
}
```

### 1.2 RecommendationService (智能推荐服务)
```java
public interface RecommendationService {
    
    /**
     * 获取高校推荐
     */
    RecommendationResponse getUniversityRecommendations(RecommendationRequest request);
    
    /**
     * 获取专业推荐
     */
    List<MajorRecommendation> getMajorRecommendations(Integer userId);
    
    /**
     * 保存用户偏好
     */
    void saveUserPreferences(Integer userId, UserPreferenceRequest request);
    
    /**
     * 获取用户偏好
     */
    UserPreference getUserPreferences(Integer userId);
}

@Service
@Transactional
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private UniversityRepository universityRepository;
    
    @Autowired
    private UniversityMajorRepository universityMajorRepository;
    
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private PredictionService predictionService;
    
    @Autowired
    private RecommendationAlgorithm recommendationAlgorithm;
    
    @Override
    public RecommendationResponse getUniversityRecommendations(RecommendationRequest request) {
        // 1. 获取用户偏好
        UserPreference preferences = getUserPreferences(request.getUserId());
        
        // 2. 基于分数范围筛选候选院校
        List<University> candidates = findCandidateUniversities(request, preferences);
        
        // 3. 应用推荐算法
        RecommendationResult result = recommendationAlgorithm.recommend(
            candidates, request, preferences
        );
        
        // 4. 分类推荐结果
        return categorizeRecommendations(result);
    }
    
    private List<University> findCandidateUniversities(
        RecommendationRequest request, UserPreference preferences) {
        
        // 根据分数范围和偏好筛选
        int rushMinScore = request.getUserScore() + 10;
        int safeMaxScore = request.getUserScore() - 20;
        
        return universityRepository.findByScoreRange(
            request.getProvinceId(),
            request.getSubjectTypeId(),
            request.getYear(),
            safeMaxScore,
            rushMinScore
        );
    }
}
```

### 1.3 PersonalityTestService (性格测试服务)
```java
public interface PersonalityTestService {
    
    /**
     * 获取测试题目
     */
    PersonalityTest getTestQuestions(String testType);
    
    /**
     * 提交测试答案
     */
    TestResultResponse submitTestAnswers(TestAnswerRequest request);
    
    /**
     * 获取用户测试历史
     */
    List<UserTestResult> getUserTestHistory(Integer userId);
    
    /**
     * 基于测试结果推荐专业
     */
    List<MajorRecommendation> recommendMajorsByPersonality(Integer userId, Integer testResultId);
}

@Service
@Transactional
@Slf4j
public class PersonalityTestServiceImpl implements PersonalityTestService {
    
    @Autowired
    private PersonalityTestRepository personalityTestRepository;
    
    @Autowired
    private UserTestResultRepository userTestResultRepository;
    
    @Autowired
    private MajorRepository majorRepository;
    
    @Autowired
    private PersonalityAnalyzer personalityAnalyzer;
    
    @Override
    public TestResultResponse submitTestAnswers(TestAnswerRequest request) {
        // 1. 验证答案完整性
        validateTestAnswers(request);
        
        // 2. 计算测试结果
        PersonalityResult result = personalityAnalyzer.analyze(
            request.getTestId(), request.getAnswers()
        );
        
        // 3. 推荐匹配专业
        List<MajorRecommendation> majorRecommendations = 
            recommendMajorsByPersonalityType(result.getPersonalityType());
        
        // 4. 保存测试结果
        UserTestResult testResult = saveTestResult(request, result, majorRecommendations);
        
        // 5. 构建响应
        return buildTestResultResponse(result, majorRecommendations);
    }
    
    private List<MajorRecommendation> recommendMajorsByPersonalityType(String personalityType) {
        // 基于性格类型匹配专业
        List<Major> allMajors = majorRepository.findAll();
        return personalityAnalyzer.matchMajors(personalityType, allMajors);
    }
}
```

## 2. Controller控制器层设计

### 2.1 PredictionController (预测控制器)
```java
@RestController
@RequestMapping("/api/prediction")
@Validated
@Slf4j
@Api(tags = "录取概率预测")
public class PredictionController {
    
    @Autowired
    private PredictionService predictionService;
    
    @PostMapping("/calculate")
    @ApiOperation("计算录取概率")
    public Result<PredictionResponse> calculatePrediction(
            @Valid @RequestBody PredictionRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // 从JWT中获取用户ID
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            request.setUserId(userId);
            
            PredictionResponse response = predictionService.calculatePrediction(request);
            return Result.success(response);
            
        } catch (BusinessException e) {
            log.error("预测计算失败: {}", e.getMessage());
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("预测计算异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
    }
    
    @PostMapping("/batch")
    @ApiOperation("批量预测")
    public Result<List<PredictionResponse>> batchPredict(
            @Valid @RequestBody BatchPredictionRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            request.setUserId(userId);
            
            List<PredictionResponse> responses = predictionService.batchPredict(request);
            return Result.success(responses);
            
        } catch (Exception e) {
            log.error("批量预测异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "批量预测失败");
        }
    }
    
    @GetMapping("/history")
    @ApiOperation("获取预测历史")
    public Result<PageResult<PredictionRecord>> getPredictionHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest httpRequest) {
        
        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page - 1, size);
            
            List<PredictionRecord> records = predictionService
                .getUserPredictionHistory(userId, pageable);
            
            return Result.success(PageResult.of(records, page, size));
            
        } catch (Exception e) {
            log.error("获取预测历史异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取历史记录失败");
        }
    }
}
```

### 2.2 RecommendationController (推荐控制器)
```java
@RestController
@RequestMapping("/api/recommendation")
@Validated
@Slf4j
@Api(tags = "智能推荐")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @PostMapping("/universities")
    @ApiOperation("获取高校推荐")
    public Result<RecommendationResponse> getUniversityRecommendations(
            @Valid @RequestBody RecommendationRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            request.setUserId(userId);
            
            RecommendationResponse response = recommendationService
                .getUniversityRecommendations(request);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取推荐异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取推荐失败");
        }
    }
    
    @PostMapping("/preferences")
    @ApiOperation("保存用户偏好")
    public Result<Void> saveUserPreferences(
            @Valid @RequestBody UserPreferenceRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            recommendationService.saveUserPreferences(userId, request);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("保存偏好异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "保存偏好失败");
        }
    }
    
    @GetMapping("/preferences")
    @ApiOperation("获取用户偏好")
    public Result<UserPreference> getUserPreferences(HttpServletRequest httpRequest) {
        
        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            UserPreference preferences = recommendationService.getUserPreferences(userId);
            
            return Result.success(preferences);
            
        } catch (Exception e) {
            log.error("获取偏好异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取偏好失败");
        }
    }
}
```

### 2.3 PersonalityTestController (性格测试控制器)
```java
@RestController
@RequestMapping("/api/personality")
@Validated
@Slf4j
@Api(tags = "性格测试")
public class PersonalityTestController {

    @Autowired
    private PersonalityTestService personalityTestService;

    @GetMapping("/test/{testType}")
    @ApiOperation("获取测试题目")
    public Result<PersonalityTest> getTestQuestions(
            @PathVariable @ApiParam("测试类型: mbti/holland/bigfive") String testType) {

        try {
            PersonalityTest test = personalityTestService.getTestQuestions(testType);
            return Result.success(test);

        } catch (Exception e) {
            log.error("获取测试题目异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取测试题目失败");
        }
    }

    @PostMapping("/test/submit")
    @ApiOperation("提交测试答案")
    public Result<TestResultResponse> submitTestAnswers(
            @Valid @RequestBody TestAnswerRequest request,
            HttpServletRequest httpRequest) {

        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            request.setUserId(userId);

            TestResultResponse response = personalityTestService.submitTestAnswers(request);
            return Result.success(response);

        } catch (Exception e) {
            log.error("提交测试答案异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "测试提交失败");
        }
    }

    @GetMapping("/history")
    @ApiOperation("获取测试历史")
    public Result<List<UserTestResult>> getUserTestHistory(HttpServletRequest httpRequest) {

        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            List<UserTestResult> history = personalityTestService.getUserTestHistory(userId);

            return Result.success(history);

        } catch (Exception e) {
            log.error("获取测试历史异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取测试历史失败");
        }
    }
}
```

### 2.4 UniversityController (高校查询控制器)
```java
@RestController
@RequestMapping("/api/universities")
@Validated
@Slf4j
@Api(tags = "高校查询")
public class UniversityController {

    @Autowired
    private UniversityService universityService;

    @GetMapping("/search")
    @ApiOperation("高校搜索")
    public Result<PageResult<UniversityResponse>> searchUniversities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) String universityType,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        try {
            UniversityQueryRequest request = UniversityQueryRequest.builder()
                .keyword(keyword)
                .provinceId(provinceId)
                .universityType(universityType)
                .level(level)
                .build();

            Pageable pageable = PageRequest.of(page - 1, size);
            PageResult<UniversityResponse> result = universityService
                .searchUniversities(request, pageable);

            return Result.success(result);

        } catch (Exception e) {
            log.error("高校搜索异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "搜索失败");
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取高校详情")
    public Result<UniversityDetailResponse> getUniversityDetail(@PathVariable Integer id) {

        try {
            UniversityDetailResponse response = universityService.getUniversityDetail(id);
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取高校详情异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取详情失败");
        }
    }
}
```

### 2.5 ScoreRankingController (一分一段控制器)
```java
@RestController
@RequestMapping("/api/score-ranking")
@Validated
@Slf4j
@Api(tags = "一分一段")
public class ScoreRankingController {

    @Autowired
    private ScoreRankingService scoreRankingService;

    @GetMapping("/query")
    @ApiOperation("查询分数排名")
    public Result<List<ScoreRankingResponse>> queryScoreRanking(
            @RequestParam Integer year,
            @RequestParam Integer provinceId,
            @RequestParam Integer subjectCategoryId,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore) {

        try {
            ScoreRankingRequest request = ScoreRankingRequest.builder()
                .year(year)
                .provinceId(provinceId)
                .subjectCategoryId(subjectCategoryId)
                .minScore(minScore)
                .maxScore(maxScore)
                .build();

            List<ScoreRankingResponse> responses = scoreRankingService.queryScoreRanking(request);
            return Result.success(responses);

        } catch (Exception e) {
            log.error("查询分数排名异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "查询失败");
        }
    }

    @GetMapping("/convert")
    @ApiOperation("分数位次转换")
    public Result<ScoreRankConvertResponse> convertScoreToRank(
            @RequestParam Integer year,
            @RequestParam Integer provinceId,
            @RequestParam Integer subjectCategoryId,
            @RequestParam Integer score) {

        try {
            ScoreRankConvertResponse response = scoreRankingService
                .convertScoreToRank(year, provinceId, subjectCategoryId, score);

            return Result.success(response);

        } catch (Exception e) {
            log.error("分数位次转换异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "转换失败");
        }
    }
}
```

### 2.6 SimilarScoreController (同分去向控制器)
```java
@RestController
@RequestMapping("/api/similar-score")
@Validated
@Slf4j
@Api(tags = "同分去向分析")
public class SimilarScoreController {

    @Autowired
    private SimilarScoreService similarScoreService;

    @GetMapping("/analysis")
    @ApiOperation("同分去向统计")
    public Result<SimilarScoreAnalysisResponse> analyzeSimilarScore(
            @RequestParam Integer score,
            @RequestParam Integer provinceId,
            @RequestParam Integer subjectCategoryId,
            @RequestParam(required = false) Integer year) {

        try {
            SimilarScoreAnalysisResponse response = similarScoreService
                .analyzeSimilarScore(score, provinceId, subjectCategoryId, year);

            return Result.success(response);

        } catch (Exception e) {
            log.error("同分去向分析异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "分析失败");
        }
    }
}
```

### 2.7 ChatController (AI对话控制器)
```java
@RestController
@RequestMapping("/api/chat")
@Validated
@Slf4j
@Api(tags = "AI对话助手")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/session")
    @ApiOperation("创建对话会话")
    public Result<ChatSessionResponse> createSession(HttpServletRequest httpRequest) {

        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            ChatSessionResponse response = chatService.createSession(userId);

            return Result.success(response);

        } catch (Exception e) {
            log.error("创建会话异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "创建会话失败");
        }
    }

    @PostMapping("/message")
    @ApiOperation("发送消息")
    public Result<ChatResponse> sendMessage(
            @Valid @RequestBody ChatRequest request,
            HttpServletRequest httpRequest) {

        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            ChatResponse response = chatService.sendMessage(userId, request);

            return Result.success(response);

        } catch (Exception e) {
            log.error("发送消息异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "发送消息失败");
        }
    }

    @GetMapping("/history/{sessionId}")
    @ApiOperation("获取对话历史")
    public Result<PageResult<ChatMessage>> getChatHistory(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size,
            HttpServletRequest httpRequest) {

        try {
            Integer userId = JwtUtil.getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page - 1, size);

            PageResult<ChatMessage> result = chatService
                .getChatHistory(userId, sessionId, pageable);

            return Result.success(result);

        } catch (Exception e) {
            log.error("获取对话历史异常", e);
            return Result.error(ErrorCode.SYSTEM_ERROR, "获取历史失败");
        }
    }
}
```

## 3. 核心算法引擎设计

### 3.1 PredictionAlgorithm (预测算法)
```java
@Component
@Slf4j
public class PredictionAlgorithm {

    /**
     * 预测录取概率
     */
    public PredictionResult predict(List<AdmissionScore> historicalScores,
                                   Integer userRank, Integer userScore) {

        // 1. 数据预处理
        List<AdmissionScore> validScores = preprocessData(historicalScores);

        // 2. 计算统计指标
        StatisticalMetrics metrics = calculateMetrics(validScores);

        // 3. 应用预测模型
        double probability = calculateProbability(userRank, userScore, metrics);

        // 4. 确定预测等级
        String level = determinePredictionLevel(probability);

        // 5. 生成详细分析
        String analysis = generateAnalysis(userRank, userScore, metrics, probability);

        return PredictionResult.builder()
            .probability(BigDecimal.valueOf(probability))
            .level(level)
            .analysis(analysis)
            .metrics(metrics)
            .build();
    }

    private double calculateProbability(Integer userRank, Integer userScore,
                                       StatisticalMetrics metrics) {

        // 基于正态分布的概率计算
        double meanRank = metrics.getAvgRank();
        double stdRank = metrics.getStdRank();

        // Z-score计算
        double zScore = (userRank - meanRank) / stdRank;

        // 转换为概率 (使用累积分布函数)
        double probability = 1.0 - normalCDF(zScore);

        // 考虑年度波动因子
        probability = adjustForYearlyVariation(probability, metrics);

        return Math.max(0.0, Math.min(1.0, probability));
    }

    private String determinePredictionLevel(double probability) {
        if (probability >= 0.8) return "保底";
        if (probability >= 0.5) return "稳妥";
        if (probability >= 0.2) return "冲刺";
        return "困难";
    }
}
```
