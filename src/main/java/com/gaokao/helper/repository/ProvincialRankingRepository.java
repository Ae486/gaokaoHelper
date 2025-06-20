package com.gaokao.helper.repository;

import com.gaokao.helper.entity.ProvincialRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 省份一分一段数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface ProvincialRankingRepository extends JpaRepository<ProvincialRanking, Long> {

    /**
     * 根据年份、省份ID和科类ID查找排名记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 排名记录列表
     */
    List<ProvincialRanking> findByYearAndProvinceIdAndSubjectCategoryId(
            Integer year, Integer provinceId, Integer subjectCategoryId);

    /**
     * 根据年份、省份ID、科类ID和分数查找排名记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param score 分数
     * @return 排名记录
     */
    Optional<ProvincialRanking> findByYearAndProvinceIdAndSubjectCategoryIdAndScore(
            Integer year, Integer provinceId, Integer subjectCategoryId, Integer score);

    /**
     * 根据年份和省份ID查找排名记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @return 排名记录列表
     */
    List<ProvincialRanking> findByYearAndProvinceId(Integer year, Integer provinceId);

    /**
     * 根据年份、省份ID和科类ID查找分数范围内的排名记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param minScore 最低分数
     * @param maxScore 最高分数
     * @return 排名记录列表
     */
    @Query("SELECT pr FROM ProvincialRanking pr WHERE pr.year = :year AND pr.provinceId = :provinceId " +
           "AND pr.subjectCategoryId = :subjectCategoryId AND pr.score BETWEEN :minScore AND :maxScore " +
           "ORDER BY pr.score DESC")
    List<ProvincialRanking> findByScoreRange(@Param("year") Integer year, 
                                           @Param("provinceId") Integer provinceId,
                                           @Param("subjectCategoryId") Integer subjectCategoryId,
                                           @Param("minScore") Integer minScore, 
                                           @Param("maxScore") Integer maxScore);

    /**
     * 根据年份、省份ID、科类ID和累计人数范围查找排名记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param minRank 最低排名
     * @param maxRank 最高排名
     * @return 排名记录列表
     */
    @Query("SELECT pr FROM ProvincialRanking pr WHERE pr.year = :year AND pr.provinceId = :provinceId " +
           "AND pr.subjectCategoryId = :subjectCategoryId AND pr.cumulativeCount BETWEEN :minRank AND :maxRank " +
           "ORDER BY pr.cumulativeCount ASC")
    List<ProvincialRanking> findByRankRange(@Param("year") Integer year, 
                                          @Param("provinceId") Integer provinceId,
                                          @Param("subjectCategoryId") Integer subjectCategoryId,
                                          @Param("minRank") Integer minRank, 
                                          @Param("maxRank") Integer maxRank);

    /**
     * 获取指定条件下的最高分数
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 最高分数
     */
    @Query("SELECT MAX(pr.score) FROM ProvincialRanking pr WHERE pr.year = :year " +
           "AND pr.provinceId = :provinceId AND pr.subjectCategoryId = :subjectCategoryId")
    Optional<Integer> findMaxScore(@Param("year") Integer year, 
                                  @Param("provinceId") Integer provinceId,
                                  @Param("subjectCategoryId") Integer subjectCategoryId);

    /**
     * 获取指定条件下的最低分数
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 最低分数
     */
    @Query("SELECT MIN(pr.score) FROM ProvincialRanking pr WHERE pr.year = :year " +
           "AND pr.provinceId = :provinceId AND pr.subjectCategoryId = :subjectCategoryId")
    Optional<Integer> findMinScore(@Param("year") Integer year, 
                                  @Param("provinceId") Integer provinceId,
                                  @Param("subjectCategoryId") Integer subjectCategoryId);
}
