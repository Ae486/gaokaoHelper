package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.SchoolRecommendation;
import com.gaokao.helper.dto.request.RecommendationRequest;
import com.gaokao.helper.dto.response.RecommendationResponse;
import com.gaokao.helper.entity.AdmissionScore;
import com.gaokao.helper.entity.Province;
import com.gaokao.helper.entity.ProvincialRanking;
import com.gaokao.helper.entity.School;
import com.gaokao.helper.entity.SubjectCategory;
import com.gaokao.helper.repository.AdmissionScoreRepository;
import com.gaokao.helper.repository.ProvinceRepository;
import com.gaokao.helper.repository.ProvincialRankingRepository;
import com.gaokao.helper.repository.SchoolRepository;
import com.gaokao.helper.repository.SubjectCategoryRepository;
import com.gaokao.helper.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学校推荐服务实现类
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final AdmissionScoreRepository admissionScoreRepository;
    private final SchoolRepository schoolRepository;
    private final ProvinceRepository provinceRepository;
    private final SubjectCategoryRepository subjectCategoryRepository;
    private final ProvincialRankingRepository provincialRankingRepository;

    @Override
    public RecommendationResponse getBasicRecommendations(RecommendationRequest request) {
        log.info("开始基础推荐: userScore={}, provinceId={}, subjectCategoryId={}, year={}", 
                request.getUserScore(), request.getProvinceId(), request.getSubjectCategoryId(), request.getYear());

        // 1. 验证参数并获取基础信息
        Province province = getProvinceById(request.getProvinceId());
        SubjectCategory subjectCategory = getSubjectCategoryById(request.getSubjectCategoryId());

        // 2. 估算用户位次
        Integer estimatedRank = estimateUserRank(request.getUserScore(), request.getProvinceId(), 
                request.getSubjectCategoryId(), request.getYear());

        // 3. 筛选候选学校 - 获取所有符合条件的学校，不限制数量
        List<SchoolRecommendation> candidateSchools = filterSchoolsByScore(
                request.getUserScore(), request.getProvinceId(), request.getSubjectCategoryId(),
                request.getYear(), Integer.MAX_VALUE); // 获取所有候选学校用于分类

        if (candidateSchools.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的推荐学校");
        }

        // 4. 应用额外筛选条件
        candidateSchools = applyAdditionalFilters(candidateSchools, request);

        // 5. 分析数据质量
        analyzeDataQuality(candidateSchools);

        // 6. 分类学校（冲刺、稳妥、保底）
        List<SchoolRecommendation> classifiedSchools = classifySchoolsByProbability(
                candidateSchools, request.getUserScore(), estimatedRank);

        // 6.5. 分析概率分布
        analyzeProbabilityDistribution(classifiedSchools);

        // 6.6. 过滤掉低于30%概率的学校，只保留有意义的推荐
        List<SchoolRecommendation> filteredSchools = classifiedSchools.stream()
                .filter(school -> school.getAdmissionProbability() >= 30.0)
                .collect(Collectors.toList());

        log.info("过滤后推荐学校数: {}所 (过滤前: {}所)", filteredSchools.size(), classifiedSchools.size());

        // 7. 按类型分组学校，不限制数量，返回所有符合条件的学校
        Map<SchoolRecommendation.RecommendationType, List<SchoolRecommendation>> schoolsByType =
                filteredSchools.stream()
                        .collect(Collectors.groupingBy(SchoolRecommendation::getRecommendationType));

        // 获取所有分类的学校，按分数从高到低排序
        List<SchoolRecommendation> rushSchools = schoolsByType
                .getOrDefault(SchoolRecommendation.RecommendationType.RUSH, new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(SchoolRecommendation::getHistoricalMinScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        List<SchoolRecommendation> stableSchools = schoolsByType
                .getOrDefault(SchoolRecommendation.RecommendationType.STABLE, new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(SchoolRecommendation::getHistoricalMinScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        List<SchoolRecommendation> safeSchools = schoolsByType
                .getOrDefault(SchoolRecommendation.RecommendationType.SAFE, new ArrayList<>())
                .stream()
                .sorted(Comparator.comparing(SchoolRecommendation::getHistoricalMinScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // 7. 构建响应
        return buildRecommendationResponse(request, province, subjectCategory, estimatedRank,
                rushSchools, stableSchools, safeSchools);
    }

    @Override
    public List<SchoolRecommendation> classifySchoolsByProbability(List<SchoolRecommendation> schools,
                                                                  Integer userScore, Integer userRank) {
        for (SchoolRecommendation school : schools) {
            // 计算位次差距（只有当位次数据有效时才计算）
            if (userRank != null && school.getHistoricalMinRank() != null && school.getHistoricalMinRank() > 0) {
                school.setRankDifference(userRank - school.getHistoricalMinRank());
                log.debug("学校{}: 位次差距 = {} - {} = {}",
                        school.getSchoolName(), userRank, school.getHistoricalMinRank(), school.getRankDifference());
            } else {
                school.setRankDifference(null); // 位次数据无效时设为null
                log.debug("学校{}: 位次数据无效，无法计算位次差距", school.getSchoolName());
            }

            // 计算录取概率
            double probability = calculateAdmissionProbability(school, userScore, userRank);
            school.setAdmissionProbability(probability);

            // 使用动态阈值进行分类
            classifySchoolByDynamicThreshold(school, probability, userRank);
        }

        return schools;
    }

    @Override
    public List<SchoolRecommendation> filterSchoolsByScore(Integer userScore, Integer provinceId,
                                                          Integer subjectCategoryId, Integer year,
                                                          Integer limit) {
        log.info("筛选学校: 不限制分数范围, 省份ID={}, 科类ID={}, 年份={}",
                provinceId, subjectCategoryId, year);

        // 获取该省份该科目的所有录取分数数据，不限制分数范围
        List<AdmissionScore> admissionScores = admissionScoreRepository
                .findByProvinceIdAndSubjectCategoryIdAndYear(provinceId, subjectCategoryId, year)
                .stream()
                .filter(score -> score.getMinScore() != null)
                .collect(Collectors.toList());

        if (admissionScores.isEmpty()) {
            log.warn("未找到符合分数范围的录取记录");
            return new ArrayList<>();
        }

        // 转换为推荐对象并统计数据质量
        List<SchoolRecommendation> recommendations = new ArrayList<>();
        int validRankCount = 0;
        int invalidRankCount = 0;

        for (AdmissionScore admissionScore : admissionScores) {
            try {
                School school = getSchoolById(admissionScore.getSchoolId());
                SchoolRecommendation recommendation = convertToRecommendation(school, admissionScore, userScore);
                recommendations.add(recommendation);

                // 统计位次数据质量
                if (admissionScore.getMinRank() != null && admissionScore.getMinRank() > 0) {
                    validRankCount++;
                } else {
                    invalidRankCount++;
                }
            } catch (Exception e) {
                log.warn("转换学校推荐失败: schoolId={}", admissionScore.getSchoolId(), e);
            }
        }

        log.info("数据质量统计: 有效位次数据{}条, 无效位次数据{}条", validRankCount, invalidRankCount);

        // 按分数差距排序并限制数量
        return recommendations.stream()
                .sorted(Comparator.comparing(SchoolRecommendation::getScoreDifference).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取省份信息
     */
    private Province getProvinceById(Integer provinceId) {
        return provinceRepository.findById(provinceId)
                .orElseThrow(() -> BusinessException.notFound("省份不存在: " + provinceId));
    }

    /**
     * 根据ID获取科类信息
     */
    private SubjectCategory getSubjectCategoryById(Integer subjectCategoryId) {
        return subjectCategoryRepository.findById(subjectCategoryId)
                .orElseThrow(() -> BusinessException.notFound("科类不存在: " + subjectCategoryId));
    }

    /**
     * 根据ID获取学校信息
     */
    private School getSchoolById(Integer schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> BusinessException.notFound("学校不存在: " + schoolId));
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
     * 应用额外筛选条件
     */
    private List<SchoolRecommendation> applyAdditionalFilters(List<SchoolRecommendation> schools,
                                                             RecommendationRequest request) {
        return schools.stream()
                .filter(school -> {
                    // 学校类型筛选
                    if (request.getSchoolType() != null && !request.getSchoolType().isEmpty()) {
                        return request.getSchoolType().equals(school.getSchoolType());
                    }
                    return true;
                })
                .filter(school -> {
                    // 公办学校筛选
                    if (request.getPublicOnly() != null && request.getPublicOnly()) {
                        // 这里需要根据实际的学校数据结构来判断是否为公办
                        // 暂时跳过此筛选
                        return true;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算录取概率（智能选择位次或分数）
     */
    private double calculateAdmissionProbability(SchoolRecommendation school, Integer userScore, Integer userRank) {
        // 检查学校位次数据是否有效（不为null且不为0）
        boolean hasValidRank = school.getHistoricalMinRank() != null && school.getHistoricalMinRank() > 0;

        // 优先使用分数计算（当学校分数数据有效时）
        if (school.getHistoricalMinScore() != null) {
            log.debug("使用分数计算录取概率: 用户分数={}, 学校最低分={}", userScore, school.getHistoricalMinScore());
            return calculateProbabilityByScore(userScore, school.getHistoricalMinScore());
        }

        // 降级使用位次计算（当分数数据无效或缺失时）
        if (userRank != null && hasValidRank) {
            log.debug("分数数据无效，使用位次计算录取概率: 用户位次={}, 学校最低位次={}", userRank, school.getHistoricalMinRank());
            return calculateProbabilityByRank(userRank, school.getHistoricalMinRank());
        }

        log.warn("学校{}既无有效位次数据也无分数数据，使用默认概率", school.getSchoolName());
        return 50.0; // 默认概率
    }

    /**
     * 基于位次计算录取概率（备选方案）
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
     * 基于分数计算录取概率（首选方案）- 精细化分数差距概率计算
     */
    private double calculateProbabilityByScore(Integer userScore, Integer schoolMinScore) {
        int scoreDiff = userScore - schoolMinScore;

        // 超精细的分数差距概率计算，确保有足够的区分度
        if (scoreDiff >= 50) {
            return 95.0;  // 超过50分，几乎稳上
        } else if (scoreDiff >= 40) {
            return 92.0;  // 超过40分，非常稳妥
        } else if (scoreDiff >= 30) {
            return 88.0;  // 超过30分，很稳妥
        } else if (scoreDiff >= 25) {
            return 85.0;  // 超过25分，稳妥
        } else if (scoreDiff >= 20) {
            return 82.0;  // 超过20分，比较稳妥
        } else if (scoreDiff >= 15) {
            return 78.0;  // 超过15分，较稳妥
        } else if (scoreDiff >= 10) {
            return 74.0;  // 超过10分，有一定把握
        } else if (scoreDiff >= 5) {
            return 68.0;  // 超过5分，有把握
        } else if (scoreDiff >= 0) {
            return 62.0;  // 刚好达线，有希望
        } else if (scoreDiff >= -3) {
            return 55.0;  // 差3分以内，还有机会
        } else if (scoreDiff >= -5) {
            return 48.0;  // 差5分以内，机会较小
        } else if (scoreDiff >= -8) {
            return 42.0;  // 差8分以内，机会很小
        } else if (scoreDiff >= -10) {
            return 35.0;  // 差10分以内，冲刺有望
        } else if (scoreDiff >= -15) {
            return 28.0;  // 差15分以内，冲刺困难
        } else if (scoreDiff >= -20) {
            return 22.0;  // 差20分以内，冲刺很困难
        } else if (scoreDiff >= -25) {
            return 16.0;  // 差25分以内，极难冲刺
        } else if (scoreDiff >= -30) {
            return 12.0;  // 差30分以内，几乎不可能
        } else if (scoreDiff >= -40) {
            return 8.0;   // 差40分以内，基本不可能
        } else {
            return 5.0;   // 差距过大，不建议冲刺
        }
    }

    /**
     * 限制学校数量
     */
    private List<SchoolRecommendation> limitSchools(List<SchoolRecommendation> schools, int limit) {
        return schools.stream()
                .sorted(Comparator.comparing(SchoolRecommendation::getAdmissionProbability).reversed())
                .limit(Math.max(1, limit))
                .collect(Collectors.toList());
    }

    /**
     * 转换为推荐对象
     */
    private SchoolRecommendation convertToRecommendation(School school, AdmissionScore admissionScore, Integer userScore) {
        SchoolRecommendation recommendation = new SchoolRecommendation();

        recommendation.setSchoolId(school.getId());
        recommendation.setSchoolName(school.getName());
        recommendation.setSchoolType(school.getSchoolType());
        recommendation.setTags(school.getTags());

        // 添加完整的学校信息
        recommendation.setSchoolLevel(school.getSchoolLevel());
        recommendation.setOwnershipType(school.getOwnershipType());
        recommendation.setSchoolAffiliation(school.getSchoolAffiliation());
        recommendation.setSchoolIntroduction(school.getSchoolIntroduction());
        recommendation.setSchoolNationalProfessions(school.getSchoolNationalProfessions());

        // 获取学校所在省份
        String schoolProvince = "未知";
        if (school.getProvinceId() != null) {
            try {
                Province province = getProvinceById(school.getProvinceId());
                schoolProvince = province.getName();
            } catch (Exception e) {
                log.warn("获取学校省份失败: schoolId={}", school.getId());
            }
        }
        recommendation.setSchoolProvince(schoolProvince);

        recommendation.setHistoricalMinScore(admissionScore.getMinScore() != null ?
                admissionScore.getMinScore().intValue() : null);
        recommendation.setHistoricalMinRank(admissionScore.getMinRank());

        // 计算分数和位次差距
        if (admissionScore.getMinScore() != null) {
            recommendation.setScoreDifference(userScore - admissionScore.getMinScore().intValue());
        }

        // 计算位次差距（需要用户位次参数，这里先设置为null，在调用处计算）
        // 位次差距将在classifySchoolsByProbability方法中计算

        return recommendation;
    }

    /**
     * 构建推荐响应
     */
    private RecommendationResponse buildRecommendationResponse(RecommendationRequest request,
                                                              Province province,
                                                              SubjectCategory subjectCategory,
                                                              Integer estimatedRank,
                                                              List<SchoolRecommendation> rushSchools,
                                                              List<SchoolRecommendation> stableSchools,
                                                              List<SchoolRecommendation> safeSchools) {

        // 构建用户信息
        RecommendationResponse.UserInfo userInfo = new RecommendationResponse.UserInfo(
                request.getUserScore(),
                province.getName(),
                subjectCategory.getName(),
                request.getYear(),
                estimatedRank
        );

        // 转换学校推荐列表
        List<RecommendationResponse.SchoolRecommendation> rushList = rushSchools.stream()
                .map(this::convertToResponseRecommendation)
                .collect(Collectors.toList());

        List<RecommendationResponse.SchoolRecommendation> stableList = stableSchools.stream()
                .map(this::convertToResponseRecommendation)
                .collect(Collectors.toList());

        List<RecommendationResponse.SchoolRecommendation> safeList = safeSchools.stream()
                .map(this::convertToResponseRecommendation)
                .collect(Collectors.toList());

        // 构建统计信息
        int totalCount = rushList.size() + stableList.size() + safeList.size();
        double averageProbability = 0.0;

        if (totalCount > 0) {
            List<SchoolRecommendation> allSchools = new ArrayList<>();
            allSchools.addAll(rushSchools);
            allSchools.addAll(stableSchools);
            allSchools.addAll(safeSchools);

            averageProbability = allSchools.stream()
                    .mapToDouble(SchoolRecommendation::getAdmissionProbability)
                    .average()
                    .orElse(0.0);
        }

        String recommendation = generateRecommendationAdvice(rushList.size(), stableList.size(), safeList.size());

        RecommendationResponse.RecommendationStatistics statistics =
                new RecommendationResponse.RecommendationStatistics(
                        rushList.size(), stableList.size(), safeList.size(),
                        totalCount, averageProbability, recommendation);

        return new RecommendationResponse(userInfo, rushList, stableList, safeList, statistics);
    }

    /**
     * 转换为响应推荐对象
     */
    private RecommendationResponse.SchoolRecommendation convertToResponseRecommendation(SchoolRecommendation school) {
        RecommendationResponse.SchoolRecommendation response = new RecommendationResponse.SchoolRecommendation();

        response.setSchoolId(school.getSchoolId());
        response.setSchoolName(school.getSchoolName());
        response.setSchoolType(school.getSchoolType());
        response.setSchoolProvince(school.getSchoolProvince());
        response.setHistoricalMinScore(school.getHistoricalMinScore());
        response.setHistoricalMinRank(school.getHistoricalMinRank());
        response.setAdmissionProbability(school.getAdmissionProbability());
        response.setScoreDifference(school.getScoreDifference());
        response.setRankDifference(school.getRankDifference());
        response.setRecommendationReason(school.getRecommendationReason());
        response.setRiskLevel(school.getRiskLevel().name());
        response.setTags(school.getTags());

        // 添加完整的学校信息
        response.setSchoolLevel(school.getSchoolLevel());
        response.setOwnershipType(school.getOwnershipType());
        response.setSchoolAffiliation(school.getSchoolAffiliation());
        response.setSchoolIntroduction(school.getSchoolIntroduction());
        response.setSchoolNationalProfessions(school.getSchoolNationalProfessions());

        // 添加省份ID（从学校信息中获取）
        try {
            School schoolEntity = getSchoolById(school.getSchoolId());
            response.setProvinceId(schoolEntity.getProvinceId());
        } catch (Exception e) {
            log.warn("获取学校省份ID失败: schoolId={}", school.getSchoolId());
            response.setProvinceId(null);
        }

        return response;
    }

    /**
     * 生成详细的推荐理由（智能选择位次或分数分析）
     */
    private String generateRecommendationReason(SchoolRecommendation school, Integer userRank, String category) {
        StringBuilder reason = new StringBuilder();

        // 检查是否有有效的位次数据
        boolean hasValidRank = userRank != null && school.getHistoricalMinRank() != null && school.getHistoricalMinRank() > 0;

        if (hasValidRank) {
            // 优先使用位次分析
            int rankDiff = userRank.intValue() - school.getHistoricalMinRank().intValue();

            if (rankDiff <= -2000) {
                reason.append("【位次分析】您的位次比该校历年最低位次优秀").append(Math.abs(rankDiff)).append("名以上，");
            } else if (rankDiff <= 0) {
                reason.append("【位次分析】您的位次比该校历年最低位次优秀").append(Math.abs(rankDiff)).append("名，");
            } else if (rankDiff <= 1000) {
                reason.append("【位次分析】您的位次比该校历年最低位次落后").append(rankDiff).append("名，");
            } else {
                reason.append("【位次分析】您的位次比该校历年最低位次落后").append(rankDiff).append("名，");
            }
        } else {
            // 降级使用分数分析
            if (school.getScoreDifference() != null) {
                int scoreDiff = school.getScoreDifference();
                if (scoreDiff >= 15) {
                    reason.append("【分数分析】您的分数比该校历年最低分高").append(scoreDiff).append("分，优势明显，");
                } else if (scoreDiff >= 0) {
                    reason.append("【分数分析】您的分数比该校历年最低分高").append(scoreDiff).append("分，刚好达线，");
                } else {
                    reason.append("【分数分析】您的分数比该校历年最低分低").append(Math.abs(scoreDiff)).append("分，略有不足，");
                }
            } else {
                reason.append("【数据不足】该校录取数据不完整，");
            }
        }

        // 添加类别建议
        switch (category) {
            case "保底":
                reason.append("建议作为保底选择，录取把握很大");
                break;
            case "稳妥":
                reason.append("建议作为主要目标，录取概率较高");
                break;
            case "冲刺":
                reason.append("可以冲刺尝试，但要做好备选准备");
                break;
        }

        return reason.toString();
    }

    /**
     * 使用优化的概率阈值对学校进行分类
     * 30%-59%为冲刺，60%-89%为稳妥，90%以上为保底
     * 低于30%的学校将被过滤掉，不进行推荐
     */
    private void classifySchoolByDynamicThreshold(SchoolRecommendation school, double probability, Integer userRank) {
        if (probability >= 90) {
            // 高概率：保底学校（≥90%）
            school.setRecommendationType(SchoolRecommendation.RecommendationType.SAFE);
            school.setRiskLevel(SchoolRecommendation.RiskLevel.LOW);
            school.setRecommendationReason(generateRecommendationReason(school, userRank, "保底"));
        } else if (probability >= 60) {
            // 中等概率：稳妥学校（60%-89%）
            school.setRecommendationType(SchoolRecommendation.RecommendationType.STABLE);
            school.setRiskLevel(SchoolRecommendation.RiskLevel.MEDIUM);
            school.setRecommendationReason(generateRecommendationReason(school, userRank, "稳妥"));
        } else if (probability >= 30) {
            // 低概率：冲刺学校（30%-59%）
            school.setRecommendationType(SchoolRecommendation.RecommendationType.RUSH);
            school.setRiskLevel(SchoolRecommendation.RiskLevel.HIGH);
            school.setRecommendationReason(generateRecommendationReason(school, userRank, "冲刺"));
        } else {
            // 极低概率：不推荐（<30%）- 这些学校将在后续被过滤掉
            // 临时设置为冲刺类型，但会在过滤步骤中被移除
            school.setRecommendationType(SchoolRecommendation.RecommendationType.RUSH);
            school.setRiskLevel(SchoolRecommendation.RiskLevel.HIGH);
            school.setRecommendationReason("录取概率过低，不建议填报");
        }
    }



    /**
     * 分析数据质量并生成统计信息
     */
    private void analyzeDataQuality(List<SchoolRecommendation> schools) {
        int totalSchools = schools.size();
        int validRankSchools = 0;
        int scoreOnlySchools = 0;

        for (SchoolRecommendation school : schools) {
            if (school.getHistoricalMinRank() != null && school.getHistoricalMinRank() > 0) {
                validRankSchools++;
            } else {
                scoreOnlySchools++;
            }
        }

        double rankDataRate = totalSchools > 0 ? (double) validRankSchools / totalSchools * 100 : 0;

        log.info("推荐数据质量分析:");
        log.info("- 总推荐学校数: {}", totalSchools);
        log.info("- 有位次数据学校: {} ({}%)", validRankSchools, String.format("%.1f", rankDataRate));
        log.info("- 仅分数数据学校: {} ({}%)", scoreOnlySchools, String.format("%.1f", 100 - rankDataRate));

        if (rankDataRate < 50) {
            log.warn("位次数据覆盖率较低({}%)，推荐精度可能受影响", String.format("%.1f", rankDataRate));
        }
    }

    /**
     * 分析概率分布并优化分类阈值
     */
    private void analyzeProbabilityDistribution(List<SchoolRecommendation> schools) {
        if (schools.isEmpty()) {
            return;
        }

        // 统计概率分布
        double[] probabilities = schools.stream()
                .mapToDouble(SchoolRecommendation::getAdmissionProbability)
                .sorted()
                .toArray();

        double minProb = probabilities[0];
        double maxProb = probabilities[probabilities.length - 1];
        double avgProb = java.util.Arrays.stream(probabilities).average().orElse(0.0);
        double medianProb = probabilities[probabilities.length / 2];

        log.info("概率分布分析:");
        log.info("- 最低概率: {}%", String.format("%.1f", minProb));
        log.info("- 最高概率: {}%", String.format("%.1f", maxProb));
        log.info("- 平均概率: {}%", String.format("%.1f", avgProb));
        log.info("- 中位数概率: {}%", String.format("%.1f", medianProb));

        // 统计各概率段的学校数量（按新的分类标准）
        long safeProb = java.util.Arrays.stream(probabilities).filter(p -> p >= 90).count();
        long stableProb = java.util.Arrays.stream(probabilities).filter(p -> p >= 60 && p < 90).count();
        long rushProb = java.util.Arrays.stream(probabilities).filter(p -> p >= 30 && p < 60).count();
        long highRiskProb = java.util.Arrays.stream(probabilities).filter(p -> p < 30).count();

        log.info("概率分段统计:");
        log.info("- 保底学校(≥90%): {}所", safeProb);
        log.info("- 稳妥学校(60-89%): {}所", stableProb);
        log.info("- 冲刺学校(30-59%): {}所", rushProb);
        log.info("- 高风险冲刺(<30%): {}所", highRiskProb);
    }

    /**
     * 生成推荐建议
     */
    private String generateRecommendationAdvice(int rushCount, int stableCount, int safeCount) {
        if (stableCount == 0 && safeCount == 0) {
            return "建议降低期望，选择更多稳妥和保底学校";
        } else if (rushCount == 0) {
            return "可以适当增加一些冲刺学校，争取更好的录取结果";
        } else if (safeCount == 0) {
            return "建议增加保底学校，确保有学校可上";
        } else {
            return "推荐结构合理，建议重点关注稳妥类学校";
        }
    }
}
