package com.gaokao.helper.repository;

import com.gaokao.helper.entity.UniversityRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 大学排名数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface UniversityRankingRepository extends JpaRepository<UniversityRanking, Integer> {

    /**
     * 根据学校ID查找排名记录
     * 
     * @param schoolId 学校ID
     * @return 排名记录列表
     */
    List<UniversityRanking> findBySchoolId(Integer schoolId);

    /**
     * 根据排名机构查找排名记录
     * 
     * @param provider 排名机构
     * @return 排名记录列表
     */
    List<UniversityRanking> findByProvider(String provider);

    /**
     * 根据年份查找排名记录
     * 
     * @param year 年份
     * @return 排名记录列表
     */
    List<UniversityRanking> findByYear(Integer year);

    /**
     * 根据学校ID和年份查找排名记录
     * 
     * @param schoolId 学校ID
     * @param year 年份
     * @return 排名记录列表
     */
    List<UniversityRanking> findBySchoolIdAndYear(Integer schoolId, Integer year);

    /**
     * 根据学校ID、排名机构和年份查找排名记录
     * 
     * @param schoolId 学校ID
     * @param provider 排名机构
     * @param year 年份
     * @return 排名记录
     */
    Optional<UniversityRanking> findBySchoolIdAndProviderAndYear(Integer schoolId, String provider, Integer year);

    /**
     * 根据排名机构和年份查找排名记录，按排名升序排列
     * 
     * @param provider 排名机构
     * @param year 年份
     * @return 排名记录列表
     */
    @Query("SELECT ur FROM UniversityRanking ur WHERE ur.provider = :provider AND ur.year = :year ORDER BY ur.rank ASC")
    List<UniversityRanking> findByProviderAndYearOrderByRankAsc(@Param("provider") String provider, @Param("year") Integer year);

    /**
     * 获取学校的最新排名记录
     * 
     * @param schoolId 学校ID
     * @param provider 排名机构
     * @return 最新排名记录
     */
    @Query("SELECT ur FROM UniversityRanking ur WHERE ur.schoolId = :schoolId AND ur.provider = :provider ORDER BY ur.year DESC")
    List<UniversityRanking> findLatestRankingBySchoolIdAndProvider(@Param("schoolId") Integer schoolId, @Param("provider") String provider);
}
