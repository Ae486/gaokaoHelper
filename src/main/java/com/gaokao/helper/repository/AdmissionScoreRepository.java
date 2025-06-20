package com.gaokao.helper.repository;

import com.gaokao.helper.entity.AdmissionScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 历年专业录取分数数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface AdmissionScoreRepository extends JpaRepository<AdmissionScore, Long> {

    /**
     * 根据学校ID查找录取分数记录
     * 
     * @param schoolId 学校ID
     * @return 录取分数记录列表
     */
    List<AdmissionScore> findBySchoolId(Integer schoolId);

    /**
     * 根据年份查找录取分数记录
     * 
     * @param year 年份
     * @return 录取分数记录列表
     */
    List<AdmissionScore> findByYear(Integer year);

    /**
     * 根据省份ID查找录取分数记录
     * 
     * @param provinceId 省份ID
     * @return 录取分数记录列表
     */
    List<AdmissionScore> findByProvinceId(Integer provinceId);

    /**
     * 根据科类ID查找录取分数记录
     * 
     * @param subjectCategoryId 科类ID
     * @return 录取分数记录列表
     */
    List<AdmissionScore> findBySubjectCategoryId(Integer subjectCategoryId);

    /**
     * 根据年份、学校ID、省份ID和科类ID查找录取分数记录
     * 
     * @param year 年份
     * @param schoolId 学校ID
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 录取分数记录
     */
    Optional<AdmissionScore> findByYearAndSchoolIdAndProvinceIdAndSubjectCategoryId(
            Integer year, Integer schoolId, Integer provinceId, Integer subjectCategoryId);

    /**
     * 根据学校ID、省份ID和科类ID查找录取分数记录
     * 
     * @param schoolId 学校ID
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 录取分数记录列表
     */
    List<AdmissionScore> findBySchoolIdAndProvinceIdAndSubjectCategoryId(
            Integer schoolId, Integer provinceId, Integer subjectCategoryId);

    /**
     * 根据学校ID、省份ID、科类ID查找历年录取分数记录，按年份降序排列
     * 
     * @param schoolId 学校ID
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @return 录取分数记录列表
     */
    @Query("SELECT as FROM AdmissionScore as WHERE as.schoolId = :schoolId AND as.provinceId = :provinceId " +
           "AND as.subjectCategoryId = :subjectCategoryId ORDER BY as.year DESC")
    List<AdmissionScore> findHistoricalScores(@Param("schoolId") Integer schoolId,
                                             @Param("provinceId") Integer provinceId,
                                             @Param("subjectCategoryId") Integer subjectCategoryId);

    /**
     * 根据年份、省份ID、科类ID和分数范围查找录取分数记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param minScore 最低分数
     * @param maxScore 最高分数
     * @return 录取分数记录列表
     */
    @Query("SELECT as FROM AdmissionScore as WHERE as.year = :year AND as.provinceId = :provinceId " +
           "AND as.subjectCategoryId = :subjectCategoryId AND as.minScore BETWEEN :minScore AND :maxScore " +
           "ORDER BY as.minScore DESC")
    List<AdmissionScore> findByScoreRange(@Param("year") Integer year,
                                        @Param("provinceId") Integer provinceId,
                                        @Param("subjectCategoryId") Integer subjectCategoryId,
                                        @Param("minScore") Float minScore,
                                        @Param("maxScore") Float maxScore);

    /**
     * 根据年份、省份ID、科类ID和位次范围查找录取分数记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param minRank 最低位次
     * @param maxRank 最高位次
     * @return 录取分数记录列表
     */
    @Query("SELECT as FROM AdmissionScore as WHERE as.year = :year AND as.provinceId = :provinceId " +
           "AND as.subjectCategoryId = :subjectCategoryId AND as.minRank BETWEEN :minRank AND :maxRank " +
           "ORDER BY as.minRank ASC")
    List<AdmissionScore> findByRankRange(@Param("year") Integer year,
                                       @Param("provinceId") Integer provinceId,
                                       @Param("subjectCategoryId") Integer subjectCategoryId,
                                       @Param("minRank") Integer minRank,
                                       @Param("maxRank") Integer maxRank);

    /**
     * 根据年份、省份ID和科类ID查找同分录取记录
     * 
     * @param year 年份
     * @param provinceId 省份ID
     * @param subjectCategoryId 科类ID
     * @param score 分数
     * @return 录取分数记录列表
     */
    @Query("SELECT as FROM AdmissionScore as WHERE as.year = :year AND as.provinceId = :provinceId " +
           "AND as.subjectCategoryId = :subjectCategoryId AND as.minScore = :score")
    List<AdmissionScore> findBySameScore(@Param("year") Integer year,
                                       @Param("provinceId") Integer provinceId,
                                       @Param("subjectCategoryId") Integer subjectCategoryId,
                                       @Param("score") Float score);
}
