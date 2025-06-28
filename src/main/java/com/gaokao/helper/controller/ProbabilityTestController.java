package com.gaokao.helper.controller;

import com.gaokao.helper.entity.AdmissionScore;
import com.gaokao.helper.entity.ProvincialRanking;
import com.gaokao.helper.entity.School;
import com.gaokao.helper.repository.AdmissionScoreRepository;
import com.gaokao.helper.repository.ProvincialRankingRepository;
import com.gaokao.helper.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 录取概率测试控制器
 * 专门用于测试概率计算算法的准确性
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class ProbabilityTestController {

    private final AdmissionScoreRepository admissionScoreRepository;
    private final SchoolRepository schoolRepository;
    private final ProvincialRankingRepository provincialRankingRepository;

    /**
     * 测试580分河南理科在所有学校中的录取概率
     */
    @GetMapping("/probability-analysis")
    public ProbabilityAnalysisResult analyzeProbability(
            @RequestParam(defaultValue = "580") Integer userScore,
            @RequestParam(defaultValue = "1") Integer provinceId,
            @RequestParam(defaultValue = "1") Integer subjectCategoryId,
            @RequestParam(defaultValue = "2024") Integer year) {
        
        log.info("开始概率分析测试: userScore={}, provinceId={}, subjectCategoryId={}, year={}", 
                userScore, provinceId, subjectCategoryId, year);

        // 1. 估算用户位次
        Integer userRank = estimateUserRank(userScore, provinceId, subjectCategoryId, year);
        log.info("用户位次: {}", userRank);

        // 2. 获取所有录取数据
        List<AdmissionScore> allAdmissionScores = admissionScoreRepository
                .findByProvinceIdAndSubjectCategoryIdAndYear(provinceId, subjectCategoryId, year);
        
        log.info("找到录取数据: {}条", allAdmissionScores.size());

        // 3. 分析每个学校的录取概率
        List<SchoolProbabilityInfo> schoolProbabilities = new ArrayList<>();
        int validRankCount = 0;
        int invalidRankCount = 0;

        for (AdmissionScore admissionScore : allAdmissionScores) {
            try {
                School school = schoolRepository.findById(admissionScore.getSchoolId()).orElse(null);
                if (school == null) {
                    continue;
                }

                SchoolProbabilityInfo info = new SchoolProbabilityInfo();
                info.setSchoolId(school.getId());
                info.setSchoolName(school.getName());
                info.setMinScore(admissionScore.getMinScore() != null ? admissionScore.getMinScore().intValue() : null);
                info.setMinRank(admissionScore.getMinRank());
                info.setUserScore(userScore);
                info.setUserRank(userRank);

                // 计算分数差
                if (info.getMinScore() != null) {
                    info.setScoreDifference(userScore - info.getMinScore());
                }

                // 计算位次差
                if (userRank != null && info.getMinRank() != null && info.getMinRank() > 0) {
                    info.setRankDifference(userRank - info.getMinRank());
                    validRankCount++;
                } else {
                    invalidRankCount++;
                }

                // 计算录取概率
                double probability = calculateAdmissionProbability(info, userScore, userRank);
                info.setProbability(probability);

                // 确定计算方法
                if (userRank != null && info.getMinRank() != null && info.getMinRank() > 0) {
                    info.setCalculationMethod("位次计算");
                } else if (info.getMinScore() != null) {
                    info.setCalculationMethod("分数计算");
                } else {
                    info.setCalculationMethod("默认概率");
                }

                schoolProbabilities.add(info);

            } catch (Exception e) {
                log.warn("处理学校数据失败: schoolId={}", admissionScore.getSchoolId(), e);
            }
        }

        // 4. 按录取概率排序
        schoolProbabilities.sort(Comparator.comparing(SchoolProbabilityInfo::getProbability).reversed());

        // 5. 统计分析
        ProbabilityAnalysisResult result = new ProbabilityAnalysisResult();
        result.setUserScore(userScore);
        result.setUserRank(userRank);
        result.setTotalSchools(schoolProbabilities.size());
        result.setValidRankCount(validRankCount);
        result.setInvalidRankCount(invalidRankCount);
        result.setSchoolProbabilities(schoolProbabilities);

        // 概率分布统计
        if (!schoolProbabilities.isEmpty()) {
            result.setMinProbability(schoolProbabilities.stream().mapToDouble(SchoolProbabilityInfo::getProbability).min().orElse(0));
            result.setMaxProbability(schoolProbabilities.stream().mapToDouble(SchoolProbabilityInfo::getProbability).max().orElse(0));
            result.setAvgProbability(schoolProbabilities.stream().mapToDouble(SchoolProbabilityInfo::getProbability).average().orElse(0));
        }

        // 分段统计
        long safeCount = schoolProbabilities.stream().filter(s -> s.getProbability() >= 90).count();
        long stableCount = schoolProbabilities.stream().filter(s -> s.getProbability() >= 60 && s.getProbability() < 90).count();
        long rushCount = schoolProbabilities.stream().filter(s -> s.getProbability() >= 30 && s.getProbability() < 60).count();
        long highRiskCount = schoolProbabilities.stream().filter(s -> s.getProbability() < 30).count();

        result.setSafeSchoolCount(safeCount);
        result.setStableSchoolCount(stableCount);
        result.setRushSchoolCount(rushCount);
        result.setHighRiskSchoolCount(highRiskCount);

        log.info("概率分析完成: 总学校数={}, 有效位次数据={}, 无效位次数据={}", 
                result.getTotalSchools(), validRankCount, invalidRankCount);
        log.info("概率分布: 最低={}%, 最高={}%, 平均={}%", 
                result.getMinProbability(), result.getMaxProbability(), result.getAvgProbability());
        log.info("分段统计: 保底={}所, 稳妥={}所, 冲刺={}所, 高风险={}所", 
                safeCount, stableCount, rushCount, highRiskCount);

        return result;
    }

    /**
     * 估算用户位次
     */
    private Integer estimateUserRank(Integer userScore, Integer provinceId, Integer subjectCategoryId, Integer year) {
        try {
            // 查找用户分数对应的位次
            ProvincialRanking ranking = provincialRankingRepository
                    .findByYearAndProvinceIdAndSubjectCategoryIdAndScore(year, provinceId, subjectCategoryId, userScore)
                    .orElse(null);

            if (ranking != null) {
                return ranking.getCumulativeCount();
            }

            // 如果没有精确匹配，查找最接近的分数
            List<ProvincialRanking> rankings = provincialRankingRepository
                    .findByYearAndProvinceIdAndSubjectCategoryId(year, provinceId, subjectCategoryId);

            if (rankings.isEmpty()) {
                log.warn("未找到位次数据，使用默认估算");
                return userScore * 100; // 简单估算
            }

            // 找到最接近的分数并插值估算
            ProvincialRanking closest = rankings.stream()
                    .min(Comparator.comparing(r -> Math.abs(r.getScore() - userScore)))
                    .orElse(rankings.get(0));

            return closest.getCumulativeCount();

        } catch (Exception e) {
            log.warn("估算用户位次失败，使用默认值", e);
            return userScore * 100; // 简单估算
        }
    }

    /**
     * 计算录取概率（复制RecommendationServiceImpl的逻辑）
     */
    private double calculateAdmissionProbability(SchoolProbabilityInfo school, Integer userScore, Integer userRank) {
        // 检查学校位次数据是否有效（不为null且不为0）
        boolean hasValidRank = school.getMinRank() != null && school.getMinRank() > 0;

        // 优先使用位次计算（当用户位次和学校位次都有效时）
        if (userRank != null && hasValidRank) {
            return calculateProbabilityByRank(userRank, school.getMinRank());
        }

        // 降级使用分数计算（当位次数据无效或缺失时）
        if (school.getMinScore() != null) {
            return calculateProbabilityByScore(userScore, school.getMinScore());
        }

        return 50.0; // 默认概率
    }

    /**
     * 基于位次计算录取概率（复制RecommendationServiceImpl的逻辑）
     */
    private double calculateProbabilityByRank(Integer userRank, Integer schoolMinRank) {
        // 位次差距 = 用户位次 - 学校历年最低位次
        // 注意：位次越小越好，所以用户位次小于学校位次表示优势
        int rankDiff = userRank - schoolMinRank;

        // 超精细的位次差距概率计算，确保有足够的区分度
        if (rankDiff <= -20000) {
            // 用户位次比学校最低位次好20000名以上 -> 绝对保底
            return 98.0;
        } else if (rankDiff <= -10000) {
            // 用户位次比学校最低位次好10000-20000名 -> 录取把握极大
            return 95.0;
        } else if (rankDiff <= -5000) {
            // 用户位次比学校最低位次好5000-10000名 -> 录取把握很大
            return 88.0;
        } else if (rankDiff <= -3000) {
            // 用户位次比学校最低位次好3000-5000名 -> 录取概率很高
            return 82.0;
        } else if (rankDiff <= -2000) {
            // 用户位次比学校最低位次好2000-3000名 -> 录取概率高
            return 75.0;
        } else if (rankDiff <= -1000) {
            // 用户位次比学校最低位次好1000-2000名 -> 录取概率较高
            return 68.0;
        } else if (rankDiff <= -500) {
            // 用户位次比学校最低位次好500-1000名 -> 录取概率适中
            return 60.0;
        } else if (rankDiff <= -200) {
            // 用户位次比学校最低位次好200-500名 -> 录取有一定把握
            return 52.0;
        } else if (rankDiff <= 0) {
            // 用户位次比学校最低位次好0-200名 -> 录取概率一般
            return 45.0;
        } else if (rankDiff <= 500) {
            // 用户位次比学校最低位次差0-500名 -> 录取有风险
            return 38.0;
        } else if (rankDiff <= 1000) {
            // 用户位次比学校最低位次差500-1000名 -> 录取风险较大
            return 30.0;
        } else if (rankDiff <= 2000) {
            // 用户位次比学校最低位次差1000-2000名 -> 录取风险大
            return 22.0;
        } else if (rankDiff <= 5000) {
            // 用户位次比学校最低位次差2000-5000名 -> 录取风险很大
            return 15.0;
        } else {
            // 用户位次比学校最低位次差5000名以上 -> 录取希望渺茫
            return 8.0;
        }
    }

    /**
     * 基于分数计算录取概率（复制RecommendationServiceImpl的逻辑）
     */
    private double calculateProbabilityByScore(Integer userScore, Integer schoolMinScore) {
        int scoreDiff = userScore - schoolMinScore;

        // 基于分数差距的概率计算（保持原有逻辑作为备选）
        if (scoreDiff >= 30) {
            return 90.0;
        } else if (scoreDiff >= 15) {
            return 75.0;
        } else if (scoreDiff >= 0) {
            return 60.0;
        } else if (scoreDiff >= -15) {
            return 40.0;
        } else if (scoreDiff >= -30) {
            return 25.0;
        } else {
            return 10.0;
        }
    }

    // 数据传输对象
    public static class ProbabilityAnalysisResult {
        private Integer userScore;
        private Integer userRank;
        private Integer totalSchools;
        private Integer validRankCount;
        private Integer invalidRankCount;
        private Double minProbability;
        private Double maxProbability;
        private Double avgProbability;
        private Long safeSchoolCount;
        private Long stableSchoolCount;
        private Long rushSchoolCount;
        private Long highRiskSchoolCount;
        private List<SchoolProbabilityInfo> schoolProbabilities;

        // Getters and Setters
        public Integer getUserScore() { return userScore; }
        public void setUserScore(Integer userScore) { this.userScore = userScore; }
        public Integer getUserRank() { return userRank; }
        public void setUserRank(Integer userRank) { this.userRank = userRank; }
        public Integer getTotalSchools() { return totalSchools; }
        public void setTotalSchools(Integer totalSchools) { this.totalSchools = totalSchools; }
        public Integer getValidRankCount() { return validRankCount; }
        public void setValidRankCount(Integer validRankCount) { this.validRankCount = validRankCount; }
        public Integer getInvalidRankCount() { return invalidRankCount; }
        public void setInvalidRankCount(Integer invalidRankCount) { this.invalidRankCount = invalidRankCount; }
        public Double getMinProbability() { return minProbability; }
        public void setMinProbability(Double minProbability) { this.minProbability = minProbability; }
        public Double getMaxProbability() { return maxProbability; }
        public void setMaxProbability(Double maxProbability) { this.maxProbability = maxProbability; }
        public Double getAvgProbability() { return avgProbability; }
        public void setAvgProbability(Double avgProbability) { this.avgProbability = avgProbability; }
        public Long getSafeSchoolCount() { return safeSchoolCount; }
        public void setSafeSchoolCount(Long safeSchoolCount) { this.safeSchoolCount = safeSchoolCount; }
        public Long getStableSchoolCount() { return stableSchoolCount; }
        public void setStableSchoolCount(Long stableSchoolCount) { this.stableSchoolCount = stableSchoolCount; }
        public Long getRushSchoolCount() { return rushSchoolCount; }
        public void setRushSchoolCount(Long rushSchoolCount) { this.rushSchoolCount = rushSchoolCount; }
        public Long getHighRiskSchoolCount() { return highRiskSchoolCount; }
        public void setHighRiskSchoolCount(Long highRiskSchoolCount) { this.highRiskSchoolCount = highRiskSchoolCount; }
        public List<SchoolProbabilityInfo> getSchoolProbabilities() { return schoolProbabilities; }
        public void setSchoolProbabilities(List<SchoolProbabilityInfo> schoolProbabilities) { this.schoolProbabilities = schoolProbabilities; }
    }

    public static class SchoolProbabilityInfo {
        private Integer schoolId;
        private String schoolName;
        private Integer minScore;
        private Integer minRank;
        private Integer userScore;
        private Integer userRank;
        private Integer scoreDifference;
        private Integer rankDifference;
        private Double probability;
        private String calculationMethod;

        // Getters and Setters
        public Integer getSchoolId() { return schoolId; }
        public void setSchoolId(Integer schoolId) { this.schoolId = schoolId; }
        public String getSchoolName() { return schoolName; }
        public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
        public Integer getMinScore() { return minScore; }
        public void setMinScore(Integer minScore) { this.minScore = minScore; }
        public Integer getMinRank() { return minRank; }
        public void setMinRank(Integer minRank) { this.minRank = minRank; }
        public Integer getUserScore() { return userScore; }
        public void setUserScore(Integer userScore) { this.userScore = userScore; }
        public Integer getUserRank() { return userRank; }
        public void setUserRank(Integer userRank) { this.userRank = userRank; }
        public Integer getScoreDifference() { return scoreDifference; }
        public void setScoreDifference(Integer scoreDifference) { this.scoreDifference = scoreDifference; }
        public Integer getRankDifference() { return rankDifference; }
        public void setRankDifference(Integer rankDifference) { this.rankDifference = rankDifference; }
        public Double getProbability() { return probability; }
        public void setProbability(Double probability) { this.probability = probability; }
        public String getCalculationMethod() { return calculationMethod; }
        public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
    }
}
