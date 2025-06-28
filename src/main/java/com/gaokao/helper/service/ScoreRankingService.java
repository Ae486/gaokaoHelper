package com.gaokao.helper.service;

import com.gaokao.helper.dto.request.RankToScoreRequest;
import com.gaokao.helper.dto.request.ScoreRankingRequest;
import com.gaokao.helper.dto.request.ScoreToRankRequest;
import com.gaokao.helper.dto.response.RankToScoreResponse;
import com.gaokao.helper.dto.response.ScoreRankingResponse;
import com.gaokao.helper.dto.response.ScoreToRankResponse;

/**
 * 一分一段查询服务接口
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
public interface ScoreRankingService {

    /**
     * 查询分数分布数据
     * 根据年份、省份ID、科类ID查询分数分布数据，支持分数范围筛选
     * 
     * @param request 查询请求参数
     * @return 分数分布响应数据
     */
    ScoreRankingResponse queryScoreDistribution(ScoreRankingRequest request);

    /**
     * 分数转位次
     * 输入分数，返回对应的位次信息
     * 
     * @param request 分数转位次请求参数
     * @return 位次信息响应
     */
    ScoreToRankResponse convertScoreToRank(ScoreToRankRequest request);

    /**
     * 位次转分数
     * 输入位次，返回对应的分数信息
     * 
     * @param request 位次转分数请求参数
     * @return 分数信息响应
     */
    RankToScoreResponse convertRankToScore(RankToScoreRequest request);

    /**
     * 获取分数段统计分析
     * 获取指定条件下的分数统计信息，包括最高分、最低分、总人数等
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 统计信息
     */
    ScoreRankingResponse.Statistics getScoreStatistics(Integer year, Integer provinceId, Integer subjectCategoryId);
}
