package com.gaokao.helper.service;

import com.gaokao.helper.dto.request.AdmissionScoreRequest;
import com.gaokao.helper.dto.request.ScoreTrendRequest;
import com.gaokao.helper.dto.response.AdmissionScoreResponse;
import com.gaokao.helper.dto.response.ScoreTrendResponse;

/**
 * 录取分数查询服务接口
 * 
 * @author PLeiA
 * @since 2024-06-26
 */
public interface AdmissionScoreService {

    /**
     * 按学校ID查询历年录取分数
     * 根据学校ID查询该学校在各省份、各科类的历年录取分数数据
     * 
     * @param request 查询请求参数
     * @return 录取分数响应数据
     */
    AdmissionScoreResponse getHistoricalScoresBySchool(AdmissionScoreRequest request);

    /**
     * 按专业查询录取分数
     * 注：当前数据库设计中暂无专业表，此方法按学校实现
     * 
     * @param request 查询请求参数
     * @return 录取分数响应数据
     */
    AdmissionScoreResponse getScoresByMajor(AdmissionScoreRequest request);

    /**
     * 录取分数趋势分析
     * 分析指定学校在特定省份和科类的录取分数变化趋势
     * 
     * @param request 趋势分析请求参数
     * @return 趋势分析响应数据
     */
    ScoreTrendResponse analyzeScoreTrend(ScoreTrendRequest request);
}
