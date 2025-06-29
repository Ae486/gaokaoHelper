package com.gaokao.helper.repository;

import com.gaokao.helper.entity.MbtiMajorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MBTI专业匹配Repository
 * 
 * @author PLeiA
 * @since 2024-06-29
 */
@Repository
public interface MbtiMajorMappingRepository extends JpaRepository<MbtiMajorMapping, Integer> {

    /**
     * 根据MBTI类型查找匹配的专业，按匹配度降序排列
     */
    @Query("SELECT m FROM MbtiMajorMapping m WHERE m.mbtiType = :mbtiType ORDER BY m.matchScore DESC")
    List<MbtiMajorMapping> findByMbtiTypeOrderByMatchScoreDesc(@Param("mbtiType") String mbtiType);

    /**
     * 根据MBTI类型查找前N个匹配的专业
     */
    @Query(value = "SELECT m FROM MbtiMajorMapping m WHERE m.mbtiType = :mbtiType ORDER BY m.matchScore DESC")
    List<MbtiMajorMapping> findTopByMbtiType(@Param("mbtiType") String mbtiType);

    /**
     * 根据专业ID查找所有匹配的MBTI类型
     */
    List<MbtiMajorMapping> findByMajorIdOrderByMatchScoreDesc(Integer majorId);
}
