package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.dto.request.RankToScoreRequest;
import com.gaokao.helper.dto.request.ScoreRankingRequest;
import com.gaokao.helper.dto.request.ScoreToRankRequest;
import com.gaokao.helper.dto.response.RankToScoreResponse;
import com.gaokao.helper.dto.response.ScoreRankingResponse;
import com.gaokao.helper.dto.response.ScoreToRankResponse;
import com.gaokao.helper.entity.Province;
import com.gaokao.helper.entity.ProvincialRanking;
import com.gaokao.helper.entity.SubjectCategory;
import com.gaokao.helper.repository.ProvinceRepository;
import com.gaokao.helper.repository.ProvincialRankingRepository;
import com.gaokao.helper.repository.SubjectCategoryRepository;
import com.gaokao.helper.service.ScoreRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 一分一段查询服务实现类
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreRankingServiceImpl implements ScoreRankingService {

    private final ProvincialRankingRepository provincialRankingRepository;
    private final ProvinceRepository provinceRepository;
    private final SubjectCategoryRepository subjectCategoryRepository;

    @Override
    public ScoreRankingResponse queryScoreDistribution(ScoreRankingRequest request) {
        log.info("查询分数分布数据: year={}, provinceId={}, subjectCategoryId={}, minScore={}, maxScore={}", 
                request.getYear(), request.getProvinceId(), request.getSubjectCategoryId(), 
                request.getMinScore(), request.getMaxScore());

        // 1. 验证参数
        validateQueryParameters(request);

        // 2. 获取省份和科类信息
        Province province = getProvinceById(request.getProvinceId());
        SubjectCategory subjectCategory = getSubjectCategoryById(request.getSubjectCategoryId());

        // 3. 查询数据
        List<ProvincialRanking> rankings;
        if (request.getMinScore() != null && request.getMaxScore() != null) {
            // 按分数范围查询
            rankings = provincialRankingRepository.findByScoreRange(
                    request.getYear(), request.getProvinceId(), request.getSubjectCategoryId(),
                    request.getMinScore(), request.getMaxScore());
        } else {
            // 查询全部数据
            rankings = provincialRankingRepository.findByYearAndProvinceIdAndSubjectCategoryId(
                    request.getYear(), request.getProvinceId(), request.getSubjectCategoryId());
        }

        if (rankings.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的分数分布数据");
        }

        // 4. 构建响应数据
        return buildScoreRankingResponse(request, province, subjectCategory, rankings);
    }

    @Override
    public ScoreToRankResponse convertScoreToRank(ScoreToRankRequest request) {
        log.info("分数转位次: year={}, provinceId={}, subjectCategoryId={}, score={}", 
                request.getYear(), request.getProvinceId(), request.getSubjectCategoryId(), request.getScore());

        // 1. 验证参数
        validateScoreToRankParameters(request);

        // 2. 查找精确匹配的分数
        Optional<ProvincialRanking> exactMatch = provincialRankingRepository
                .findByYearAndProvinceIdAndSubjectCategoryIdAndScore(
                        request.getYear(), request.getProvinceId(), 
                        request.getSubjectCategoryId(), request.getScore());

        if (exactMatch.isPresent()) {
            // 精确匹配
            return buildExactScoreToRankResponse(request, exactMatch.get());
        } else {
            // 近似匹配
            return buildApproximateScoreToRankResponse(request);
        }
    }

    @Override
    public RankToScoreResponse convertRankToScore(RankToScoreRequest request) {
        log.info("位次转分数: year={}, provinceId={}, subjectCategoryId={}, rank={}", 
                request.getYear(), request.getProvinceId(), request.getSubjectCategoryId(), request.getRank());

        // 1. 验证参数
        validateRankToScoreParameters(request);

        // 2. 查找最接近的位次
        List<ProvincialRanking> rankings = provincialRankingRepository
                .findByYearAndProvinceIdAndSubjectCategoryId(
                        request.getYear(), request.getProvinceId(), request.getSubjectCategoryId());

        if (rankings.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的排名数据");
        }

        // 3. 找到最接近的位次
        ProvincialRanking closestRanking = findClosestRankingByRank(rankings, request.getRank());
        
        // 4. 构建响应
        return buildRankToScoreResponse(request, closestRanking, rankings);
    }

    @Override
    public ScoreRankingResponse.Statistics getScoreStatistics(Integer year, Integer provinceId, Integer subjectCategoryId) {
        log.info("获取分数统计: year={}, provinceId={}, subjectCategoryId={}", year, provinceId, subjectCategoryId);

        // 1. 获取最高分和最低分
        Optional<Integer> maxScore = provincialRankingRepository.findMaxScore(year, provinceId, subjectCategoryId);
        Optional<Integer> minScore = provincialRankingRepository.findMinScore(year, provinceId, subjectCategoryId);

        if (maxScore.isEmpty() || minScore.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的统计数据");
        }

        // 2. 获取总人数（最大累计人数）
        List<ProvincialRanking> allRankings = provincialRankingRepository
                .findByYearAndProvinceIdAndSubjectCategoryId(year, provinceId, subjectCategoryId);

        if (allRankings.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的排名数据");
        }

        Integer totalCount = allRankings.stream()
                .mapToInt(ProvincialRanking::getCumulativeCount)
                .max()
                .orElse(0);

        return new ScoreRankingResponse.Statistics(
                maxScore.get(), minScore.get(), totalCount, allRankings.size());
    }

    /**
     * 验证查询参数
     */
    private void validateQueryParameters(ScoreRankingRequest request) {
        if (request.getMinScore() != null && request.getMaxScore() != null) {
            if (request.getMinScore() > request.getMaxScore()) {
                throw BusinessException.paramError("最低分数不能大于最高分数");
            }
        }
    }

    /**
     * 验证分数转位次参数
     */
    private void validateScoreToRankParameters(ScoreToRankRequest request) {
        // 基本验证已通过@Valid注解完成，这里可以添加业务逻辑验证
    }

    /**
     * 验证位次转分数参数
     */
    private void validateRankToScoreParameters(RankToScoreRequest request) {
        // 基本验证已通过@Valid注解完成，这里可以添加业务逻辑验证
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
     * 构建分数分布响应数据
     */
    private ScoreRankingResponse buildScoreRankingResponse(ScoreRankingRequest request,
                                                         Province province,
                                                         SubjectCategory subjectCategory,
                                                         List<ProvincialRanking> rankings) {
        // 1. 构建查询信息
        ScoreRankingResponse.QueryInfo queryInfo = new ScoreRankingResponse.QueryInfo(
                request.getYear(), request.getProvinceId(), province.getName(),
                request.getSubjectCategoryId(), subjectCategory.getName());

        // 2. 构建分数分布数据
        Integer totalCount = rankings.stream()
                .mapToInt(ProvincialRanking::getCumulativeCount)
                .max()
                .orElse(0);

        List<ScoreRankingResponse.ScoreDistribution> distributions = rankings.stream()
                .map(ranking -> {
                    double percentage = totalCount > 0 ?
                            (double) ranking.getCumulativeCount() / totalCount * 100 : 0.0;
                    return new ScoreRankingResponse.ScoreDistribution(
                            ranking.getScore(), ranking.getCountAtScore(),
                            ranking.getCumulativeCount(), percentage);
                })
                .collect(Collectors.toList());

        // 3. 构建统计信息
        ScoreRankingResponse.Statistics statistics = getScoreStatistics(
                request.getYear(), request.getProvinceId(), request.getSubjectCategoryId());

        return new ScoreRankingResponse(queryInfo, distributions, statistics);
    }

    /**
     * 构建精确匹配的分数转位次响应
     */
    private ScoreToRankResponse buildExactScoreToRankResponse(ScoreToRankRequest request,
                                                            ProvincialRanking ranking) {
        // 获取总人数用于计算百分比
        Integer totalCount = getTotalCount(request.getYear(), request.getProvinceId(),
                                         request.getSubjectCategoryId());

        double percentage = totalCount > 0 ?
                (double) ranking.getCumulativeCount() / totalCount * 100 : 0.0;

        return ScoreToRankResponse.exactMatch(
                request.getScore(), ranking.getCumulativeCount(),
                ranking.getCountAtScore(), percentage, totalCount);
    }

    /**
     * 构建近似匹配的分数转位次响应
     */
    private ScoreToRankResponse buildApproximateScoreToRankResponse(ScoreToRankRequest request) {
        // 查找最接近的分数
        List<ProvincialRanking> rankings = provincialRankingRepository
                .findByYearAndProvinceIdAndSubjectCategoryId(
                        request.getYear(), request.getProvinceId(), request.getSubjectCategoryId());

        if (rankings.isEmpty()) {
            throw BusinessException.notFound("未找到符合条件的排名数据");
        }

        ProvincialRanking closestRanking = findClosestRankingByScore(rankings, request.getScore());
        Integer totalCount = getTotalCount(request.getYear(), request.getProvinceId(),
                                         request.getSubjectCategoryId());

        double percentage = totalCount > 0 ?
                (double) closestRanking.getCumulativeCount() / totalCount * 100 : 0.0;

        String description = String.format("未找到分数%d的精确数据，使用最接近的分数%d",
                request.getScore(), closestRanking.getScore());

        return ScoreToRankResponse.approximateMatch(
                request.getScore(), closestRanking.getCumulativeCount(),
                closestRanking.getCountAtScore(), percentage, totalCount, description);
    }

    /**
     * 构建位次转分数响应
     */
    private RankToScoreResponse buildRankToScoreResponse(RankToScoreRequest request,
                                                       ProvincialRanking ranking,
                                                       List<ProvincialRanking> allRankings) {
        Integer totalCount = allRankings.stream()
                .mapToInt(ProvincialRanking::getCumulativeCount)
                .max()
                .orElse(0);

        double percentage = totalCount > 0 ?
                (double) ranking.getCumulativeCount() / totalCount * 100 : 0.0;

        boolean exactMatch = ranking.getCumulativeCount().equals(request.getRank());
        String description = exactMatch ? "精确匹配" :
                String.format("未找到位次%d的精确数据，使用最接近的位次%d",
                        request.getRank(), ranking.getCumulativeCount());

        return new RankToScoreResponse(
                request.getRank(), ranking.getScore(), ranking.getCountAtScore(),
                percentage, totalCount, exactMatch, description);
    }

    /**
     * 获取总人数
     */
    private Integer getTotalCount(Integer year, Integer provinceId, Integer subjectCategoryId) {
        List<ProvincialRanking> rankings = provincialRankingRepository
                .findByYearAndProvinceIdAndSubjectCategoryId(year, provinceId, subjectCategoryId);

        return rankings.stream()
                .mapToInt(ProvincialRanking::getCumulativeCount)
                .max()
                .orElse(0);
    }

    /**
     * 根据分数查找最接近的排名记录
     */
    private ProvincialRanking findClosestRankingByScore(List<ProvincialRanking> rankings, Integer targetScore) {
        return rankings.stream()
                .min((r1, r2) -> {
                    int diff1 = Math.abs(r1.getScore() - targetScore);
                    int diff2 = Math.abs(r2.getScore() - targetScore);
                    return Integer.compare(diff1, diff2);
                })
                .orElseThrow(() -> BusinessException.notFound("未找到匹配的排名数据"));
    }

    /**
     * 根据位次查找最接近的排名记录
     */
    private ProvincialRanking findClosestRankingByRank(List<ProvincialRanking> rankings, Integer targetRank) {
        return rankings.stream()
                .min((r1, r2) -> {
                    int diff1 = Math.abs(r1.getCumulativeCount() - targetRank);
                    int diff2 = Math.abs(r2.getCumulativeCount() - targetRank);
                    return Integer.compare(diff1, diff2);
                })
                .orElseThrow(() -> BusinessException.notFound("未找到匹配的排名数据"));
    }
}
