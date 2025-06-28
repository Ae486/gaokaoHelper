package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.request.AdmissionScoreRequest;
import com.gaokao.helper.dto.request.ScoreTrendRequest;
import com.gaokao.helper.dto.response.AdmissionScoreResponse;
import com.gaokao.helper.dto.response.ScoreTrendResponse;
import com.gaokao.helper.entity.AdmissionScore;
import com.gaokao.helper.entity.Province;
import com.gaokao.helper.entity.School;
import com.gaokao.helper.entity.SubjectCategory;
import com.gaokao.helper.repository.AdmissionScoreRepository;
import com.gaokao.helper.repository.ProvinceRepository;
import com.gaokao.helper.repository.SchoolRepository;
import com.gaokao.helper.repository.SubjectCategoryRepository;
import com.gaokao.helper.service.AdmissionScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 录取分数查询服务实现类
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdmissionScoreServiceImpl implements AdmissionScoreService {

    private final AdmissionScoreRepository admissionScoreRepository;
    private final SchoolRepository schoolRepository;
    private final ProvinceRepository provinceRepository;
    private final SubjectCategoryRepository subjectCategoryRepository;

    @Override
    public AdmissionScoreResponse getHistoricalScoresBySchool(AdmissionScoreRequest request) {
        log.info("查询学校历年录取分数: schoolId={}, provinceId={}, subjectCategoryId={}, startYear={}, endYear={}", 
                request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId(), 
                request.getStartYear(), request.getEndYear());

        // 1. 验证参数
        validateRequest(request);

        // 2. 获取学校信息
        School school = getSchoolById(request.getSchoolId());

        // 3. 查询录取分数数据
        List<AdmissionScore> admissionScores = queryAdmissionScores(request);

        if (admissionScores.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的录取分数数据");
        }

        // 4. 构建响应数据
        return buildAdmissionScoreResponse(school, admissionScores);
    }

    @Override
    public AdmissionScoreResponse getScoresByMajor(AdmissionScoreRequest request) {
        log.info("按专业查询录取分数: schoolId={}, provinceId={}, subjectCategoryId={}", 
                request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId());

        // 注：当前数据库设计中暂无专业表，此方法按学校实现
        // 后续可以扩展为真正的专业查询
        return getHistoricalScoresBySchool(request);
    }

    @Override
    public ScoreTrendResponse analyzeScoreTrend(ScoreTrendRequest request) {
        log.info("分析录取分数趋势: schoolId={}, provinceId={}, subjectCategoryId={}, yearCount={}", 
                request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId(), request.getYearCount());

        // 1. 验证参数
        validateTrendRequest(request);

        // 2. 获取基础信息
        School school = getSchoolById(request.getSchoolId());
        Province province = getProvinceById(request.getProvinceId());
        SubjectCategory subjectCategory = getSubjectCategoryById(request.getSubjectCategoryId());

        // 3. 查询趋势数据
        List<AdmissionScore> trendScores = queryTrendData(request);

        if (trendScores.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的趋势数据");
        }

        // 4. 构建趋势响应
        return buildScoreTrendResponse(school, province, subjectCategory, trendScores);
    }

    /**
     * 验证请求参数
     */
    private void validateRequest(AdmissionScoreRequest request) {
        if (request.getStartYear() != null && request.getEndYear() != null) {
            if (request.getStartYear() > request.getEndYear()) {
                throw BusinessException.paramError("开始年份不能大于结束年份");
            }
        }
    }

    /**
     * 验证趋势分析请求参数
     */
    private void validateTrendRequest(ScoreTrendRequest request) {
        // 基本验证已通过@Valid注解完成
    }

    /**
     * 根据ID获取学校信息
     */
    private School getSchoolById(Integer schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> BusinessException.notFound("学校不存在: " + schoolId));
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
     * 查询录取分数数据
     */
    private List<AdmissionScore> queryAdmissionScores(AdmissionScoreRequest request) {
        if (request.getProvinceId() != null && request.getSubjectCategoryId() != null) {
            // 按省份和科类查询
            if (request.getStartYear() != null && request.getEndYear() != null) {
                return admissionScoreRepository.findBySchoolIdAndProvinceIdAndSubjectCategoryIdAndYearBetween(
                        request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId(),
                        request.getStartYear(), request.getEndYear());
            } else {
                return admissionScoreRepository.findBySchoolIdAndProvinceIdAndSubjectCategoryId(
                        request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId());
            }
        } else if (request.getProvinceId() != null) {
            // 只按省份查询
            if (request.getStartYear() != null && request.getEndYear() != null) {
                return admissionScoreRepository.findBySchoolIdAndProvinceIdAndYearBetween(
                        request.getSchoolId(), request.getProvinceId(),
                        request.getStartYear(), request.getEndYear());
            } else {
                return admissionScoreRepository.findBySchoolIdAndProvinceId(
                        request.getSchoolId(), request.getProvinceId());
            }
        } else {
            // 只按学校查询
            if (request.getStartYear() != null && request.getEndYear() != null) {
                return admissionScoreRepository.findBySchoolIdAndYearBetween(
                        request.getSchoolId(), request.getStartYear(), request.getEndYear());
            } else {
                return admissionScoreRepository.findBySchoolId(request.getSchoolId());
            }
        }
    }

    /**
     * 查询趋势数据
     */
    private List<AdmissionScore> queryTrendData(ScoreTrendRequest request) {
        // 获取最近N年的数据
        int currentYear = 2024; // 可以从配置或系统时间获取
        int startYear = currentYear - request.getYearCount() + 1;

        return admissionScoreRepository.findBySchoolIdAndProvinceIdAndSubjectCategoryIdAndYearBetween(
                request.getSchoolId(), request.getProvinceId(), request.getSubjectCategoryId(),
                startYear, currentYear);
    }

    /**
     * 构建录取分数响应数据
     */
    private AdmissionScoreResponse buildAdmissionScoreResponse(School school, List<AdmissionScore> admissionScores) {
        // 1. 获取学校所在省份名称
        String provinceName = "未知";
        if (school.getProvinceId() != null) {
            try {
                Province province = getProvinceById(school.getProvinceId());
                provinceName = province.getName();
            } catch (Exception e) {
                log.warn("获取学校省份信息失败: schoolId={}, provinceId={}", school.getId(), school.getProvinceId());
            }
        }

        // 2. 构建学校信息
        AdmissionScoreResponse.SchoolInfo schoolInfo = new AdmissionScoreResponse.SchoolInfo(
                school.getId(), school.getName(), school.getSchoolType(), provinceName);

        // 3. 构建录取分数数据列表
        List<AdmissionScoreResponse.AdmissionScoreData> scoreDataList = admissionScores.stream()
                .map(this::convertToScoreData)
                .sorted(Comparator.comparing(AdmissionScoreResponse.AdmissionScoreData::getYear).reversed())
                .collect(Collectors.toList());

        // 4. 构建统计信息
        AdmissionScoreResponse.Statistics statistics = buildStatistics(admissionScores);

        return new AdmissionScoreResponse(schoolInfo, scoreDataList, statistics);
    }

    /**
     * 构建分数趋势响应数据
     */
    private ScoreTrendResponse buildScoreTrendResponse(School school, Province province,
                                                     SubjectCategory subjectCategory,
                                                     List<AdmissionScore> trendScores) {
        // 1. 构建学校信息
        ScoreTrendResponse.SchoolInfo schoolInfo = new ScoreTrendResponse.SchoolInfo(
                school.getId(), school.getName(), province.getName(), subjectCategory.getName());

        // 2. 按年份排序并构建趋势数据
        List<AdmissionScore> sortedScores = trendScores.stream()
                .sorted(Comparator.comparing(AdmissionScore::getYear))
                .collect(Collectors.toList());

        List<ScoreTrendResponse.TrendData> trendDataList = new ArrayList<>();
        for (int i = 0; i < sortedScores.size(); i++) {
            AdmissionScore current = sortedScores.get(i);
            AdmissionScore previous = i > 0 ? sortedScores.get(i - 1) : null;

            ScoreTrendResponse.TrendData trendData = convertToTrendData(current, previous);
            trendDataList.add(trendData);
        }

        // 3. 构建趋势分析
        ScoreTrendResponse.TrendAnalysis analysis = analyzeTrend(sortedScores);

        return new ScoreTrendResponse(schoolInfo, trendDataList, analysis);
    }

    /**
     * 转换AdmissionScore为AdmissionScoreData
     */
    private AdmissionScoreResponse.AdmissionScoreData convertToScoreData(AdmissionScore admissionScore) {
        // 获取省份和科类名称
        String provinceName = "未知";
        String subjectCategoryName = "未知";

        try {
            if (admissionScore.getProvinceId() != null) {
                Province province = getProvinceById(admissionScore.getProvinceId());
                provinceName = province.getName();
            }
            if (admissionScore.getSubjectCategoryId() != null) {
                SubjectCategory subjectCategory = getSubjectCategoryById(admissionScore.getSubjectCategoryId());
                subjectCategoryName = subjectCategory.getName();
            }
        } catch (Exception e) {
            log.warn("获取省份或科类信息失败: provinceId={}, subjectCategoryId={}",
                    admissionScore.getProvinceId(), admissionScore.getSubjectCategoryId());
        }

        return new AdmissionScoreResponse.AdmissionScoreData(
                admissionScore.getYear(),
                provinceName,
                subjectCategoryName,
                "本科一批", // 默认批次，实际数据库中暂无此字段
                admissionScore.getMinScore() != null ? admissionScore.getMinScore().intValue() : null,
                null, // maxScore字段暂无
                null, // avgScore字段暂无
                admissionScore.getMinRank(),
                null  // admissionCount字段暂无
        );
    }

    /**
     * 构建统计信息
     */
    private AdmissionScoreResponse.Statistics buildStatistics(List<AdmissionScore> admissionScores) {
        if (admissionScores.isEmpty()) {
            return new AdmissionScoreResponse.Statistics(0, "", 0, 0, 0.0);
        }

        // 计算统计数据
        int totalRecords = admissionScores.size();

        List<Integer> years = admissionScores.stream()
                .map(AdmissionScore::getYear)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        String yearRange = years.isEmpty() ? "" :
                years.get(0) + "-" + years.get(years.size() - 1);

        Integer historicalMaxScore = admissionScores.stream()
                .filter(score -> score.getMinScore() != null)
                .mapToInt(score -> score.getMinScore().intValue())
                .max()
                .orElse(0);

        Integer historicalMinScore = admissionScores.stream()
                .filter(score -> score.getMinScore() != null)
                .mapToInt(score -> score.getMinScore().intValue())
                .min()
                .orElse(0);

        Double averageScore = admissionScores.stream()
                .filter(score -> score.getMinScore() != null)
                .mapToDouble(score -> score.getMinScore().doubleValue())
                .average()
                .orElse(0.0);

        return new AdmissionScoreResponse.Statistics(
                totalRecords, yearRange, historicalMaxScore,
                historicalMinScore, averageScore);
    }

    /**
     * 转换AdmissionScore为TrendData
     */
    private ScoreTrendResponse.TrendData convertToTrendData(AdmissionScore current, AdmissionScore previous) {
        Integer scoreChange = null;
        Integer rankChange = null;

        if (previous != null) {
            if (current.getMinScore() != null && previous.getMinScore() != null) {
                scoreChange = current.getMinScore().intValue() - previous.getMinScore().intValue();
            }
            if (current.getMinRank() != null && previous.getMinRank() != null) {
                rankChange = current.getMinRank() - previous.getMinRank();
            }
        }

        return new ScoreTrendResponse.TrendData(
                current.getYear(),
                current.getMinScore() != null ? current.getMinScore().intValue() : null,
                current.getMinScore() != null ? current.getMinScore().intValue() : null, // 用minScore代替avgScore
                current.getMinRank(),
                null, // admissionCount字段暂无
                scoreChange,
                rankChange
        );
    }

    /**
     * 分析趋势
     */
    private ScoreTrendResponse.TrendAnalysis analyzeTrend(List<AdmissionScore> sortedScores) {
        if (sortedScores.size() < 2) {
            return new ScoreTrendResponse.TrendAnalysis(
                    "数据不足", 0.0, 0, 0,
                    "数据不足，无法进行趋势分析", "建议收集更多历史数据");
        }

        // 计算年度变化
        List<Integer> changes = new ArrayList<>();
        for (int i = 1; i < sortedScores.size(); i++) {
            AdmissionScore current = sortedScores.get(i);
            AdmissionScore previous = sortedScores.get(i - 1);

            if (current.getMinScore() != null && previous.getMinScore() != null) {
                int change = current.getMinScore().intValue() - previous.getMinScore().intValue();
                changes.add(change);
            }
        }

        if (changes.isEmpty()) {
            return new ScoreTrendResponse.TrendAnalysis(
                    "数据不完整", 0.0, 0, 0,
                    "分数数据不完整，无法进行趋势分析", "建议补充完整的分数数据");
        }

        // 计算统计指标
        double averageChange = changes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int maxFluctuation = changes.stream().mapToInt(Math::abs).max().orElse(0);

        // 判断总体趋势
        String overallTrend;
        if (averageChange > 2) {
            overallTrend = "上升";
        } else if (averageChange < -2) {
            overallTrend = "下降";
        } else {
            overallTrend = "稳定";
        }

        // 计算稳定性评分（波动越小，稳定性越高）
        int stabilityScore = Math.max(0, 100 - maxFluctuation * 2);

        // 生成描述和建议
        String description = String.format("该校录取分数呈%s趋势，年均变化%.1f分，最大波动%d分",
                overallTrend, averageChange, maxFluctuation);

        String recommendation;
        if ("上升".equals(overallTrend)) {
            recommendation = String.format("建议考生准备比去年高%.0f-%.0f分的成绩",
                    Math.abs(averageChange), Math.abs(averageChange) + 5);
        } else if ("下降".equals(overallTrend)) {
            recommendation = String.format("录取分数有下降趋势，但建议仍准备比去年高5分左右的成绩");
        } else {
            recommendation = "录取分数相对稳定，建议准备与去年相当或略高的成绩";
        }

        return new ScoreTrendResponse.TrendAnalysis(
                overallTrend, averageChange, maxFluctuation, stabilityScore,
                description, recommendation);
    }
}
