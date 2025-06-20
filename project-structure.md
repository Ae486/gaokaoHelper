# 高考志愿填报助手项目结构

```
gaokao-volunteer-helper/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
├── docker-compose.yml                         # Docker容器编排
├── Dockerfile                                 # Docker镜像构建
├── .gitignore                                # Git忽略文件
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gaokao/
│   │   │           └── helper/
│   │   │               ├── GaokaoHelperApplication.java    # 启动类
│   │   │               ├── config/                         # 配置类
│   │   │               │   ├── DatabaseConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── SwaggerConfig.java
│   │   │               │   └── WebConfig.java
│   │   │               ├── controller/                     # 控制器层
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── PredictionController.java
│   │   │               │   ├── RecommendationController.java
│   │   │               │   ├── PersonalityTestController.java
│   │   │               │   ├── UniversityController.java
│   │   │               │   ├── ScoreRankingController.java
│   │   │               │   ├── SimilarScoreController.java
│   │   │               │   └── ChatController.java
│   │   │               ├── service/                        # 业务逻辑层
│   │   │               │   ├── impl/                       # 实现类
│   │   │               │   │   ├── AuthServiceImpl.java
│   │   │               │   │   ├── PredictionServiceImpl.java
│   │   │               │   │   ├── RecommendationServiceImpl.java
│   │   │               │   │   ├── PersonalityTestServiceImpl.java
│   │   │               │   │   ├── UniversityServiceImpl.java
│   │   │               │   │   ├── ScoreRankingServiceImpl.java
│   │   │               │   │   ├── SimilarScoreServiceImpl.java
│   │   │               │   │   └── ChatServiceImpl.java
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── PredictionService.java
│   │   │               │   ├── RecommendationService.java
│   │   │               │   ├── PersonalityTestService.java
│   │   │               │   ├── UniversityService.java
│   │   │               │   ├── ScoreRankingService.java
│   │   │               │   ├── SimilarScoreService.java
│   │   │               │   └── ChatService.java
│   │   │               ├── repository/                     # 数据访问层
│   │   │               │   ├── ProvinceRepository.java
│   │   │               │   ├── SubjectTypeRepository.java
│   │   │               │   ├── UniversityRepository.java
│   │   │               │   ├── UniversityRankingRepository.java
│   │   │               │   ├── ScoreRankingRepository.java
│   │   │               │   ├── AdmissionScoreRepository.java
│   │   │               │   ├── UserRepository.java
│   │   │               │   ├── MajorRepository.java
│   │   │               │   ├── UniversityMajorRepository.java
│   │   │               │   ├── PersonalityTestRepository.java
│   │   │               │   ├── UserTestResultRepository.java
│   │   │               │   ├── UserPreferenceRepository.java
│   │   │               │   ├── PredictionRecordRepository.java
│   │   │               │   ├── ChatSessionRepository.java
│   │   │               │   └── ChatMessageRepository.java
│   │   │               ├── entity/                         # 实体类
│   │   │               │   ├── Province.java
│   │   │               │   ├── SubjectType.java
│   │   │               │   ├── University.java
│   │   │               │   ├── UniversityRanking.java
│   │   │               │   ├── ScoreRanking.java
│   │   │               │   ├── AdmissionScore.java
│   │   │               │   ├── User.java
│   │   │               │   ├── Major.java
│   │   │               │   ├── UniversityMajor.java
│   │   │               │   ├── PersonalityTest.java
│   │   │               │   ├── UserTestResult.java
│   │   │               │   ├── UserPreference.java
│   │   │               │   ├── PredictionRecord.java
│   │   │               │   ├── ChatSession.java
│   │   │               │   └── ChatMessage.java
│   │   │               ├── dto/                            # 数据传输对象
│   │   │               │   ├── request/                    # 请求DTO
│   │   │               │   │   ├── LoginRequest.java
│   │   │               │   │   ├── RegisterRequest.java
│   │   │               │   │   ├── PredictionRequest.java
│   │   │               │   │   ├── RecommendationRequest.java
│   │   │               │   │   ├── TestAnswerRequest.java
│   │   │               │   │   ├── UniversityQueryRequest.java
│   │   │               │   │   ├── ScoreRankingRequest.java
│   │   │               │   │   └── ChatRequest.java
│   │   │               │   └── response/                   # 响应DTO
│   │   │               │       ├── LoginResponse.java
│   │   │               │       ├── PredictionResponse.java
│   │   │               │       ├── RecommendationResponse.java
│   │   │               │       ├── TestResultResponse.java
│   │   │               │       ├── UniversityResponse.java
│   │   │               │       ├── ScoreRankingResponse.java
│   │   │               │       ├── SimilarScoreResponse.java
│   │   │               │       └── ChatResponse.java
│   │   │               ├── algorithm/                      # 算法引擎
│   │   │               │   ├── PredictionAlgorithm.java
│   │   │               │   ├── RecommendationAlgorithm.java
│   │   │               │   ├── PersonalityAnalyzer.java
│   │   │               │   └── ScoreAnalyzer.java
│   │   │               ├── util/                           # 工具类
│   │   │               │   ├── JwtUtil.java
│   │   │               │   ├── PasswordUtil.java
│   │   │               │   ├── DateUtil.java
│   │   │               │   └── ValidationUtil.java
│   │   │               ├── exception/                      # 异常处理
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── BusinessException.java
│   │   │               │   └── ErrorCode.java
│   │   │               └── common/                         # 公共类
│   │   │                   ├── Result.java                # 统一返回结果
│   │   │                   ├── PageResult.java            # 分页结果
│   │   │                   └── Constants.java             # 常量定义
│   │   └── resources/
│   │       ├── application.yml                            # 主配置文件
│   │       ├── application-dev.yml                        # 开发环境配置
│   │       ├── application-prod.yml                       # 生产环境配置
│   │       ├── db/
│   │       │   └── migration/                             # Flyway数据库迁移脚本
│   │       │       ├── V1__Create_base_tables.sql
│   │       │       ├── V2__Create_extended_tables.sql
│   │       │       └── V3__Insert_initial_data.sql
│   │       ├── static/                                    # 静态资源
│   │       └── templates/                                 # 模板文件
│   └── test/
│       └── java/
│           └── com/
│               └── gaokao/
│                   └── helper/
│                       ├── GaokaoHelperApplicationTests.java
│                       ├── controller/                    # 控制器测试
│                       ├── service/                       # 服务层测试
│                       ├── repository/                    # 数据访问层测试
│                       └── algorithm/                     # 算法测试
└── docs/                                                  # 项目文档
    ├── api/                                              # API文档
    ├── database/                                         # 数据库设计文档
    └── deployment/                                       # 部署文档
```

## 核心依赖配置 (pom.xml 主要依赖)

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Spring Cache + Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
    </dependency>
    
    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
    </dependency>
    
    <!-- Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
    </dependency>
    
    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
</dependencies>
```
