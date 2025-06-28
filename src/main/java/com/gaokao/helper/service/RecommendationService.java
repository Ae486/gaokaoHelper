package com.gaokao.helper.service;

import com.gaokao.helper.dto.SchoolRecommendation;
import com.gaokao.helper.dto.request.RecommendationRequest;
import com.gaokao.helper.dto.response.RecommendationResponse;

import java.util.List;

/**
 * 学校推荐服务接口
 *
 * @author PLeiA
 * @since 2024-06-26
 */
public interface RecommendationService {

    /**
     * 基于用户分数进行基础推荐
     * 根据用户分数、省份、科类等信息，推荐合适的学校
     *
     * @param request 推荐请求参数
     * @return 推荐响应数据
     */
    RecommendationResponse getBasicRecommendations(RecommendationRequest request);

    /**
     * 将推荐学校分为冲刺、稳妥、保底三类
     * 根据分数差距和录取概率对学校进行分类
     *
     * @param schools 候选学校列表
     * @param userScore 用户分数
     * @param userRank 用户位次
     * @return 分类后的学校推荐列表
     */
    List<SchoolRecommendation> classifySchoolsByProbability(List<SchoolRecommendation> schools,
                                                           Integer userScore, Integer userRank);

    /**
     * 根据分数范围筛选学校
     * 筛选出分数范围合适的候选学校
     *
     * @param userScore 用户分数
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param year 年份
     * @param limit 返回数量限制
     * @return 筛选后的学校列表
     */
    List<SchoolRecommendation> filterSchoolsByScore(Integer userScore, Integer provinceId,
                                                   Integer subjectCategoryId, Integer year,
                                                   Integer limit);
}
