# 🏗️ 高考志愿填报助手项目结构

## 📁 项目根目录结构

```
gaokaoHelper/                                   # 项目根目录
├── 📄 pom.xml                                 # Maven项目配置文件
├── 📖 README.md                               # 项目说明文档
├── 📋 project-structure.md                    # 项目结构说明文档
├── 📋 entity-design.md                        # 实体设计文档
├── 📋 service-controller-design.md            # 服务控制器设计文档
├── 📋 technical-challenges-and-solutions.md   # 技术难点与解决方案
├── 📋 api-design.md                           # API设计文档
├── 🔧 scripts/                               # 脚本文件目录
│   ├── backup_database.bat                   # 数据库备份脚本
│   ├── create_tables_step1.sql               # 数据库建表脚本
│   ├── insert_test_data.sql                  # 测试数据插入脚本
│   ├── safe_import_data.sql                  # 安全数据导入脚本
│   └── setup_personality_test.bat            # 性格测试数据设置脚本
├── 📊 rawDataFiles/                          # 原始数据文件
│   ├── 01模板.csv                            # 数据模板文件
│   ├── 02模板.csv                            # 数据模板文件
│   ├── merged_admission_scores.csv           # 合并后的录取分数数据
│   ├── merged_all_rankings.csv               # 合并后的排名数据
│   ├── provinces.csv                         # 省份数据
│   ├── schools.csv                           # 学校数据
│   ├── work1.py                              # 数据处理脚本1
│   ├── work2.py                              # 数据处理脚本2
│   ├── work3.py                              # 数据处理脚本3
│   ├── 一分一段/                             # 一分一段数据目录
│   └── 投档线/                               # 投档线数据目录
├── 📝 logs/                                  # 日志文件目录
│   └── gaokao-helper.log                     # 应用日志文件
├── 💾 backup/                                # 备份文件目录
│   └── gaokaodb_backup_.sql                  # 数据库备份文件
├── 📚 docs/                                  # 项目文档目录
│   └── personality-test-design.md            # 性格测试设计文档
├── 🎯 target/                                # Maven构建输出目录
│   ├── classes/                              # 编译后的类文件
│   ├── generated-sources/                    # 生成的源码
│   ├── generated-test-sources/               # 生成的测试源码
│   ├── maven-status/                         # Maven状态文件
│   └── test-classes/                         # 编译后的测试类文件
└── 🔧 测试文件/                              # 各种测试HTML文件
    ├── debug-auto-advance.html               # 自动跳转调试页面
    ├── simple-auto-test.html                 # 简单自动测试页面
    ├── test-ai-chat.ps1                      # AI聊天测试脚本
    ├── test-ai-improvements.html             # AI改进测试页面
    └── ...                                   # 其他测试文件
```

## 🏛️ 源码目录结构 (src/)

```
src/
├── main/                                      # 主源码目录
│   ├── java/com/gaokao/helper/               # Java源码包
│   │   ├── 🚀 GaokaoHelperApplication.java   # Spring Boot启动类
│   │   ├── 🧮 algorithm/                     # 核心算法包
│   │   │   ├── PredictionAlgorithm.java      # 录取概率预测算法
│   │   │   ├── RecommendationEngine.java     # 智能推荐引擎
│   │   │   └── PersonalityAnalyzer.java      # 性格分析算法
│   │   ├── 📝 annotation/                    # 自定义注解包
│   │   │   ├── RateLimit.java                # 限流注解
│   │   │   └── LogOperation.java             # 操作日志注解
│   │   ├── 🔍 aspect/                        # AOP切面包
│   │   │   ├── RateLimitAspect.java          # 限流切面
│   │   │   └── LogAspect.java                # 日志切面
│   │   ├── 🔧 common/                        # 公共组件包
│   │   │   ├── Result.java                   # 统一返回结果封装
│   │   │   ├── PageResult.java               # 分页结果封装
│   │   │   ├── Constants.java                # 系统常量定义
│   │   │   └── ErrorCode.java                # 错误码定义
│   │   ├── ⚙️ config/                        # 配置类包
│   │   │   ├── SecurityConfig.java           # Spring Security配置
│   │   │   ├── SwaggerConfig.java            # Swagger API文档配置
│   │   │   ├── WebConfig.java                # Web配置
│   │   │   ├── JpaConfig.java                # JPA配置
│   │   │   └── AsyncConfig.java              # 异步配置
│   │   ├── 🎮 controller/                    # 控制器层包
│   │   │   ├── AuthController.java           # 认证控制器
│   │   │   ├── AdminController.java          # 管理员控制器
│   │   │   ├── PredictionController.java     # 录取预测控制器
│   │   │   ├── RecommendationController.java # 智能推荐控制器
│   │   │   ├── PersonalityTestController.java # 性格测试控制器
│   │   │   ├── SchoolController.java         # 学校查询控制器
│   │   │   ├── AdmissionScoreController.java # 录取分数控制器
│   │   │   ├── RankingController.java        # 排名查询控制器
│   │   │   ├── ScoreRankingController.java   # 一分一段控制器
│   │   │   ├── ChatController.java           # AI对话控制器
│   │   │   ├── TestController.java           # 测试控制器
│   │   │   └── ProbabilityTestController.java # 概率测试控制器
│   │   ├── 📦 dto/                           # 数据传输对象包
│   │   │   ├── request/                      # 请求DTO子包
│   │   │   │   ├── LoginRequest.java         # 登录请求DTO
│   │   │   │   ├── RegisterRequest.java      # 注册请求DTO
│   │   │   │   ├── PredictionRequest.java    # 预测请求DTO
│   │   │   │   ├── RecommendationRequest.java # 推荐请求DTO
│   │   │   │   ├── TestAnswerRequest.java    # 测试答案请求DTO
│   │   │   │   ├── ChatRequest.java          # 聊天请求DTO
│   │   │   │   └── ...                       # 其他请求DTO
│   │   │   └── response/                     # 响应DTO子包
│   │   │       ├── LoginResponse.java        # 登录响应DTO
│   │   │       ├── PredictionResponse.java   # 预测响应DTO
│   │   │       ├── RecommendationResponse.java # 推荐响应DTO
│   │   │       ├── TestResultResponse.java   # 测试结果响应DTO
│   │   │       ├── ChatResponse.java         # 聊天响应DTO
│   │   │       └── ...                       # 其他响应DTO
│   │   ├── 🗃️ entity/                        # 实体类包
│   │   │   ├── User.java                     # 用户实体
│   │   │   ├── Province.java                 # 省份实体
│   │   │   ├── SubjectCategory.java          # 科类实体
│   │   │   ├── School.java                   # 学校实体
│   │   │   ├── AdmissionScore.java           # 录取分数实体
│   │   │   ├── ProvincialRanking.java        # 省份排名实体
│   │   │   ├── UniversityRanking.java        # 大学排名实体
│   │   │   ├── Major.java                    # 专业实体
│   │   │   ├── MbtiQuestion.java             # MBTI测试题目实体
│   │   │   ├── HollandQuestion.java          # 霍兰德测试题目实体
│   │   │   ├── PersonalityTestRecord.java    # 性格测试记录实体
│   │   │   ├── MbtiDescription.java          # MBTI描述实体
│   │   │   ├── MbtiMajorMapping.java         # MBTI专业匹配实体
│   │   │   ├── ChatSession.java              # 聊天会话实体
│   │   │   ├── ChatMessage.java              # 聊天消息实体
│   │   │   ├── AdminLog.java                 # 管理员日志实体
│   │   │   └── package-info.java             # 包信息文件
│   │   ├── ❌ exception/                     # 异常处理包
│   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理器
│   │   │   ├── BusinessException.java       # 业务异常类
│   │   │   ├── AuthenticationException.java # 认证异常类
│   │   │   └── ValidationException.java     # 验证异常类
│   │   ├── 🗄️ repository/                   # 数据访问层包
│   │   │   ├── UserRepository.java          # 用户数据访问接口
│   │   │   ├── ProvinceRepository.java      # 省份数据访问接口
│   │   │   ├── SubjectCategoryRepository.java # 科类数据访问接口
│   │   │   ├── SchoolRepository.java        # 学校数据访问接口
│   │   │   ├── AdmissionScoreRepository.java # 录取分数数据访问接口
│   │   │   ├── ProvincialRankingRepository.java # 省份排名数据访问接口
│   │   │   ├── UniversityRankingRepository.java # 大学排名数据访问接口
│   │   │   ├── MajorRepository.java         # 专业数据访问接口
│   │   │   ├── MbtiQuestionRepository.java  # MBTI题目数据访问接口
│   │   │   ├── HollandQuestionRepository.java # 霍兰德题目数据访问接口
│   │   │   ├── PersonalityTestRecordRepository.java # 测试记录数据访问接口
│   │   │   ├── MbtiDescriptionRepository.java # MBTI描述数据访问接口
│   │   │   ├── MbtiMajorMappingRepository.java # MBTI专业匹配数据访问接口
│   │   │   ├── ChatSessionRepository.java   # 聊天会话数据访问接口
│   │   │   ├── ChatMessageRepository.java   # 聊天消息数据访问接口
│   │   │   └── AdminLogRepository.java      # 管理员日志数据访问接口
│   │   ├── 💼 service/                       # 业务逻辑层包
│   │   │   ├── impl/                         # 业务逻辑实现子包
│   │   │   │   ├── AuthServiceImpl.java      # 认证服务实现
│   │   │   │   ├── AdminServiceImpl.java     # 管理员服务实现
│   │   │   │   ├── PredictionServiceImpl.java # 预测服务实现
│   │   │   │   ├── RecommendationServiceImpl.java # 推荐服务实现
│   │   │   │   ├── PersonalityTestServiceImpl.java # 性格测试服务实现
│   │   │   │   ├── SchoolServiceImpl.java    # 学校服务实现
│   │   │   │   ├── AdmissionScoreServiceImpl.java # 录取分数服务实现
│   │   │   │   ├── RankingServiceImpl.java   # 排名服务实现
│   │   │   │   ├── ChatServiceImpl.java      # 聊天服务实现
│   │   │   │   └── ...                       # 其他服务实现
│   │   │   ├── AuthService.java              # 认证服务接口
│   │   │   ├── AdminService.java             # 管理员服务接口
│   │   │   ├── PredictionService.java        # 预测服务接口
│   │   │   ├── RecommendationService.java    # 推荐服务接口
│   │   │   ├── PersonalityTestService.java   # 性格测试服务接口
│   │   │   ├── SchoolService.java            # 学校服务接口
│   │   │   ├── AdmissionScoreService.java    # 录取分数服务接口
│   │   │   ├── RankingService.java           # 排名服务接口
│   │   │   ├── ChatService.java              # 聊天服务接口
│   │   │   └── ...                           # 其他服务接口
│   │   └── 🛠️ util/                          # 工具类包
│   │       ├── JwtUtil.java                  # JWT工具类
│   │       ├── PasswordUtil.java             # 密码工具类
│   │       ├── DateUtil.java                 # 日期工具类
│   │       ├── ValidationUtil.java           # 验证工具类
│   │       ├── HttpUtil.java                 # HTTP工具类
│   │       └── StringUtil.java               # 字符串工具类
│   └── resources/                            # 资源文件目录
│       ├── ⚙️ application.yml                # 主配置文件
│       ├── ⚙️ application-dev.yml            # 开发环境配置
│       ├── ⚙️ application-prod.yml           # 生产环境配置
│       ├── 📊 data/                          # 初始化数据目录
│       │   ├── holland_questions.sql         # 霍兰德测试题目数据
│       │   ├── mbti_questions.sql            # MBTI测试题目数据
│       │   └── major_personality_mapping.sql # 专业性格匹配数据
│       └── 🌐 static/                        # 静态资源目录
│           ├── 🏠 index.html                 # 登录页面
│           ├── 🏠 main.html                  # 主界面
│           ├── 🏠 admin.html                 # 管理员界面
│           ├── 🏠 ai-chat-test.html          # AI聊天测试页面
│           ├── 🏠 holland-test.html          # 霍兰德测试页面
│           ├── 🏠 mbti-test.html             # MBTI测试页面
│           ├── 🏠 recommendation-new.html    # 推荐页面
│           ├── 🏠 admission-score-test.html  # 录取分数测试页面
│           ├── 🏠 debug-helper.html          # 调试助手页面
│           ├── 🎨 css/                       # 样式文件目录
│           │   ├── main.css                  # 主样式文件
│           │   ├── admin.css                 # 管理员样式文件
│           │   ├── personality-test.css      # 性格测试样式文件
│           │   ├── sidebar.css               # 侧边栏样式文件
│           │   └── style.css                 # 通用样式文件
│           ├── 📜 js/                        # JavaScript文件目录
│           │   ├── main.js                   # 主JavaScript文件
│           │   ├── admin.js                  # 管理员JavaScript文件
│           │   ├── app.js                    # 应用JavaScript文件
│           │   ├── holland-test.js           # 霍兰德测试JavaScript文件
│           │   ├── mbti-test.js              # MBTI测试JavaScript文件
│           │   └── sidebar.js                # 侧边栏JavaScript文件
│           └── 🖼️ images/                    # 图片资源目录
│               └── ai-avatar.png             # AI头像图片
└── test/                                     # 测试源码目录
    ├── java/com/gaokao/helper/               # 测试Java源码包
    │   ├── GaokaoHelperApplicationTests.java # 应用启动测试
    │   ├── controller/                       # 控制器测试包
    │   │   ├── AuthControllerTest.java       # 认证控制器测试
    │   │   ├── PredictionControllerTest.java # 预测控制器测试
    │   │   └── ...                           # 其他控制器测试
    │   ├── service/                          # 服务层测试包
    │   │   ├── AuthServiceTest.java          # 认证服务测试
    │   │   ├── PredictionServiceTest.java    # 预测服务测试
    │   │   └── ...                           # 其他服务测试
    │   ├── repository/                       # 数据访问层测试包
    │   │   ├── UserRepositoryTest.java       # 用户仓库测试
    │   │   ├── SchoolRepositoryTest.java     # 学校仓库测试
    │   │   └── ...                           # 其他仓库测试
    │   └── algorithm/                        # 算法测试包
    │       ├── PredictionAlgorithmTest.java  # 预测算法测试
    │       └── RecommendationEngineTest.java # 推荐引擎测试
    └── resources/                            # 测试资源目录
        ├── application-test.yml              # 测试环境配置
        └── test-data/                        # 测试数据目录
```

## 🔧 核心依赖配置

### Maven依赖 (pom.xml)

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>2.7.18</spring-boot.version>
    <mysql.version>8.0.33</mysql.version>
    <jwt.version>0.11.5</jwt.version>
    <hutool.version>5.8.22</hutool.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <swagger.version>1.7.0</swagger.version>
</properties>

<dependencies>
    <!-- 🌐 Spring Boot Web Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 🗄️ Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- 🔒 Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- ✅ Spring Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- 🔄 Spring AOP -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <!-- 🌐 Spring WebFlux (for HTTP Client) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- 📊 Spring Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- 🗃️ MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>${mysql.version}</version>
        <scope>runtime</scope>
    </dependency>

    <!-- 🔧 Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- 🔄 MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>

    <!-- 🛠️ Hutool 工具类 -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>${hutool.version}</version>
    </dependency>

    <!-- 🔑 JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>${jwt.version}</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>${jwt.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>${jwt.version}</version>
        <scope>runtime</scope>
    </dependency>

    <!-- 📚 Swagger/OpenAPI 3 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>${swagger.version}</version>
    </dependency>

    <!-- 🧮 Apache Commons Math -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>

    <!-- 🌐 OkHttp for HTTP requests -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.12.0</version>
    </dependency>

    <!-- 🧪 Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- 🔒 Spring Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- 🗃️ H2 Database for Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 📋 项目特点说明

### 🏗️ 架构特点

1. **分层架构**: 严格按照Controller-Service-Repository分层
2. **模块化设计**: 功能模块清晰分离，便于维护和扩展
3. **配置分离**: 支持多环境配置，开发、测试、生产环境独立
4. **异常统一处理**: 全局异常处理机制，统一错误响应格式
5. **安全认证**: 基于JWT的无状态认证机制

### 🔧 技术特色

1. **算法引擎**: 独立的算法包，包含预测、推荐、分析等核心算法
2. **AOP切面**: 使用切面编程实现日志记录、限流等横切关注点
3. **自定义注解**: 提供限流、日志等自定义注解，简化开发
4. **工具类丰富**: 完善的工具类库，提高开发效率
5. **测试完善**: 完整的单元测试和集成测试覆盖

### 📊 数据处理

1. **原始数据**: rawDataFiles目录包含各种原始数据文件
2. **数据脚本**: scripts目录包含数据库操作和数据处理脚本
3. **数据初始化**: resources/data目录包含系统初始化数据
4. **数据备份**: backup目录用于数据库备份文件存储

### 🌐 前端资源

1. **响应式设计**: 支持PC、平板、手机多端访问
2. **模块化CSS**: 按功能模块组织样式文件
3. **交互丰富**: 使用GSAP等库实现丰富的交互效果
4. **测试页面**: 提供多个测试页面用于功能验证

这个项目结构体现了现代Java Web应用的最佳实践，具有良好的可维护性、可扩展性和可测试性。
