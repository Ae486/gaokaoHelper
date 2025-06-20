# JPA实体类与Repository接口设计

## 1. 基础实体类

### 1.1 Province (省份实体)
```java
@Entity
@Table(name = "provinces")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "province_exam_type")
    private Integer provinceExamType; // 0:新高考 1:传统高考 2:其他
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 一对多关系
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
    
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScoreRanking> scoreRankings;
}
```

### 1.2 SubjectType (科类实体)
```java
@Entity
@Table(name = "subject_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(length = 20)
    private String code;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "subjectType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
}
```

### 1.3 University (院校实体)
```java
@Entity
@Table(name = "universities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 20)
    private String code;
    
    @Column(length = 50)
    private String type; // 综合/理工/师范/医药等
    
    @Column(length = 20)
    private String level; // 985/211/双一流/普通本科等
    
    @Column(length = 100)
    private String location;
    
    @Column(length = 200)
    private String website;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 一对多关系
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UniversityRanking> rankings;
    
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UniversityMajor> universityMajors;
}
```

### 1.4 User (用户实体)
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "real_name", length = 50)
    private String realName;
    
    @Column(name = "exam_year")
    private Integer examYear;
    
    @Column(name = "total_score")
    private Integer totalScore;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_type_id")
    private SubjectType subjectType;
    
    // 一对多关系
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTestResult> testResults;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreference preference;
}
```

## 2. 业务实体类

### 2.1 Major (专业实体)
```java
@Entity
@Table(name = "majors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 20)
    private String code;
    
    @Column(length = 50)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "employment_prospects", columnDefinition = "TEXT")
    private String employmentProspects;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UniversityMajor> universityMajors;
}
```

### 2.2 UniversityMajor (院校专业关联实体)
```java
@Entity
@Table(name = "university_majors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversityMajor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "min_score")
    private Integer minScore;
    
    @Column(name = "min_rank")
    private Integer minRank;
    
    @Column(name = "enrollment_plan")
    private Integer enrollmentPlan;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_type_id", nullable = false)
    private SubjectType subjectType;
}
```

### 2.3 ScoreRanking (一分一段实体)
```java
@Entity
@Table(name = "score_rankings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(name = "count_at_score", nullable = false)
    private Integer countAtScore;
    
    @Column(name = "cumulative_count", nullable = false)
    private Integer cumulativeCount;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_category_id", nullable = false)
    private SubjectType subjectType;
}
```

### 2.4 AdmissionScore (录取分数实体)
```java
@Entity
@Table(name = "admission_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "min_score")
    private Integer minScore;
    
    @Column(name = "min_rank")
    private Integer minRank;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private University university;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_category_id", nullable = false)
    private SubjectType subjectType;
}
```

## 3. 测试与偏好实体

### 3.1 PersonalityTest (性格测试实体)
```java
@Entity
@Table(name = "personality_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalityTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private String questions;
    
    @Column(name = "scoring_rules", columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private String scoringRules;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTestResult> testResults;
}
```

### 3.2 UserTestResult (用户测试结果实体)
```java
@Entity
@Table(name = "user_test_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private String answers;
    
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private String results;
    
    @Column(name = "recommended_majors", columnDefinition = "TEXT")
    private String recommendedMajors;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private PersonalityTest test;
}
```

### 3.3 UserPreference (用户偏好实体)
```java
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "preferred_regions", length = 500)
    private String preferredRegions;
    
    @Column(name = "preferred_university_types", length = 200)
    private String preferredUniversityTypes;
    
    @Column(name = "preferred_majors", length = 500)
    private String preferredMajors;
    
    @Column(name = "min_score_expectation")
    private Integer minScoreExpectation;
    
    @Column(name = "max_score_expectation")
    private Integer maxScoreExpectation;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

## 4. 预测与聊天实体

### 4.1 PredictionRecord (预测记录实体)
```java
@Entity
@Table(name = "prediction_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "prediction_probability", precision = 5, scale = 4)
    private BigDecimal predictionProbability;
    
    @Column(name = "prediction_level", length = 20)
    private String predictionLevel;
    
    @Column(name = "prediction_details", columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private String predictionDetails;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;
}
```

### 4.2 ChatSession (聊天会话实体)
```java
@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "session_id", nullable = false, unique = true, length = 100)
    private String sessionId;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;
}
```

### 4.3 ChatMessage (聊天消息实体)
```java
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType; // user/assistant
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;
}
```

## 5. JSON转换器
```java
@Converter
public class JsonConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
```

# Repository接口设计

## 1. 基础Repository接口

### 1.1 ProvinceRepository
```java
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    Optional<Province> findByName(String name);

    List<Province> findByProvinceExamType(Integer examType);

    @Query("SELECT p FROM Province p WHERE p.name LIKE %:keyword%")
    List<Province> findByNameContaining(@Param("keyword") String keyword);
}
```

### 1.2 UniversityRepository
```java
@Repository
public interface UniversityRepository extends JpaRepository<University, Integer> {

    List<University> findByNameContaining(String name);

    List<University> findByType(String type);

    List<University> findByLevel(String level);

    List<University> findByLocationContaining(String location);

    @Query("SELECT u FROM University u WHERE " +
           "(:name IS NULL OR u.name LIKE %:name%) AND " +
           "(:type IS NULL OR u.type = :type) AND " +
           "(:level IS NULL OR u.level = :level) AND " +
           "(:location IS NULL OR u.location LIKE %:location%)")
    Page<University> findByMultipleConditions(
        @Param("name") String name,
        @Param("type") String type,
        @Param("level") String level,
        @Param("location") String location,
        Pageable pageable
    );

    @Query("SELECT u FROM University u JOIN u.universityMajors um " +
           "WHERE um.province.id = :provinceId AND um.subjectType.id = :subjectTypeId " +
           "AND um.year = :year AND um.minScore BETWEEN :minScore AND :maxScore")
    List<University> findByScoreRange(
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("year") Integer year,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore
    );
}
```

### 1.3 MajorRepository
```java
@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {

    List<Major> findByNameContaining(String name);

    List<Major> findByCategory(String category);

    @Query("SELECT m FROM Major m WHERE m.category IN :categories")
    List<Major> findByCategoryIn(@Param("categories") List<String> categories);

    @Query("SELECT m FROM Major m JOIN m.universityMajors um " +
           "WHERE um.university.id = :universityId")
    List<Major> findByUniversityId(@Param("universityId") Integer universityId);
}
```

## 2. 业务Repository接口

### 2.1 ScoreRankingRepository
```java
@Repository
public interface ScoreRankingRepository extends JpaRepository<ScoreRanking, Integer> {

    List<ScoreRanking> findByYearAndProvinceIdAndSubjectTypeId(
        Integer year, Integer provinceId, Integer subjectTypeId
    );

    @Query("SELECT sr FROM ScoreRanking sr WHERE " +
           "sr.year = :year AND sr.province.id = :provinceId AND " +
           "sr.subjectType.id = :subjectTypeId AND " +
           "sr.score BETWEEN :minScore AND :maxScore " +
           "ORDER BY sr.score DESC")
    List<ScoreRanking> findByScoreRange(
        @Param("year") Integer year,
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore
    );

    @Query("SELECT sr FROM ScoreRanking sr WHERE " +
           "sr.year = :year AND sr.province.id = :provinceId AND " +
           "sr.subjectType.id = :subjectTypeId AND sr.score = :score")
    Optional<ScoreRanking> findByExactScore(
        @Param("year") Integer year,
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("score") Integer score
    );

    @Query("SELECT sr FROM ScoreRanking sr WHERE " +
           "sr.year = :year AND sr.province.id = :provinceId AND " +
           "sr.subjectType.id = :subjectTypeId AND " +
           "sr.cumulativeCount >= :rank " +
           "ORDER BY sr.cumulativeCount ASC")
    List<ScoreRanking> findByRank(
        @Param("year") Integer year,
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("rank") Integer rank,
        Pageable pageable
    );
}
```

### 2.2 AdmissionScoreRepository
```java
@Repository
public interface AdmissionScoreRepository extends JpaRepository<AdmissionScore, Integer> {

    List<AdmissionScore> findByUniversityIdAndProvinceIdAndSubjectTypeId(
        Integer universityId, Integer provinceId, Integer subjectTypeId
    );

    @Query("SELECT as FROM AdmissionScore as WHERE " +
           "as.university.id = :universityId AND as.province.id = :provinceId AND " +
           "as.subjectType.id = :subjectTypeId AND as.year IN :years " +
           "ORDER BY as.year DESC")
    List<AdmissionScore> findHistoricalScores(
        @Param("universityId") Integer universityId,
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("years") List<Integer> years
    );

    @Query("SELECT as FROM AdmissionScore as WHERE " +
           "as.province.id = :provinceId AND as.subjectType.id = :subjectTypeId AND " +
           "as.year = :year AND as.minScore = :score")
    List<AdmissionScore> findBySameScore(
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("year") Integer year,
        @Param("score") Integer score
    );

    @Query("SELECT as FROM AdmissionScore as WHERE " +
           "as.province.id = :provinceId AND as.subjectType.id = :subjectTypeId AND " +
           "as.year = :year AND as.minScore BETWEEN :minScore AND :maxScore")
    List<AdmissionScore> findByScoreRange(
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("year") Integer year,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore
    );
}
```

### 2.3 UniversityMajorRepository
```java
@Repository
public interface UniversityMajorRepository extends JpaRepository<UniversityMajor, Integer> {

    List<UniversityMajor> findByUniversityIdAndProvinceIdAndSubjectTypeIdAndYear(
        Integer universityId, Integer provinceId, Integer subjectTypeId, Integer year
    );

    @Query("SELECT um FROM UniversityMajor um WHERE " +
           "um.university.id = :universityId AND um.major.id = :majorId AND " +
           "um.province.id = :provinceId AND um.subjectType.id = :subjectTypeId AND " +
           "um.year IN :years ORDER BY um.year DESC")
    List<UniversityMajor> findHistoricalData(
        @Param("universityId") Integer universityId,
        @Param("majorId") Integer majorId,
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("years") List<Integer> years
    );

    @Query("SELECT um FROM UniversityMajor um WHERE " +
           "um.province.id = :provinceId AND um.subjectType.id = :subjectTypeId AND " +
           "um.year = :year AND um.minScore BETWEEN :minScore AND :maxScore")
    List<UniversityMajor> findByScoreRange(
        @Param("provinceId") Integer provinceId,
        @Param("subjectTypeId") Integer subjectTypeId,
        @Param("year") Integer year,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore
    );
}
```

## 3. 用户相关Repository

### 3.1 UserRepository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByProvinceIdAndSubjectTypeIdAndExamYear(
        Integer provinceId, Integer subjectTypeId, Integer examYear
    );
}
```

### 3.2 UserTestResultRepository
```java
@Repository
public interface UserTestResultRepository extends JpaRepository<UserTestResult, Integer> {

    List<UserTestResult> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<UserTestResult> findByUserIdAndTestId(Integer userId, Integer testId);

    @Query("SELECT utr FROM UserTestResult utr WHERE utr.user.id = :userId " +
           "ORDER BY utr.createdAt DESC")
    List<UserTestResult> findLatestResultsByUserId(@Param("userId") Integer userId, Pageable pageable);
}
```

### 3.3 PredictionRecordRepository
```java
@Repository
public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Integer> {

    List<PredictionRecord> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<PredictionRecord> findByUserIdAndUniversityId(Integer userId, Integer universityId);

    @Query("SELECT pr FROM PredictionRecord pr WHERE pr.user.id = :userId " +
           "ORDER BY pr.createdAt DESC")
    List<PredictionRecord> findRecentPredictions(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(pr) FROM PredictionRecord pr WHERE " +
           "pr.university.id = :universityId AND pr.predictionLevel = :level")
    Long countByUniversityAndLevel(
        @Param("universityId") Integer universityId,
        @Param("level") String level
    );
}
```

## 4. 聊天相关Repository

### 4.1 ChatSessionRepository
```java
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    Optional<ChatSession> findBySessionId(String sessionId);

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(Integer userId);

    @Query("SELECT cs FROM ChatSession cs WHERE cs.user.id = :userId " +
           "ORDER BY cs.updatedAt DESC")
    List<ChatSession> findRecentSessions(@Param("userId") Integer userId, Pageable pageable);
}
```

### 4.2 ChatMessageRepository
```java
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(Integer sessionId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session.id = :sessionId " +
           "ORDER BY cm.createdAt ASC")
    Page<ChatMessage> findBySessionIdWithPaging(@Param("sessionId") Integer sessionId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.session.id = :sessionId")
    Long countBySessionId(@Param("sessionId") Integer sessionId);
}
```
