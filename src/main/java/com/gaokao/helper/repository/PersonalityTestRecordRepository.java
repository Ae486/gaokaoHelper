package com.gaokao.helper.repository;

import com.gaokao.helper.entity.PersonalityTestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 性格测试记录Repository
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Repository
public interface PersonalityTestRecordRepository extends JpaRepository<PersonalityTestRecord, Integer> {

    /**
     * 根据用户ID获取测试记录
     */
    List<PersonalityTestRecord> findByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * 根据测试类型获取记录
     */
    List<PersonalityTestRecord> findByTestTypeOrderByCreatedAtDesc(PersonalityTestRecord.TestType testType);

    /**
     * 根据用户ID和测试类型获取最新记录
     */
    PersonalityTestRecord findFirstByUserIdAndTestTypeOrderByCreatedAtDesc(Integer userId, PersonalityTestRecord.TestType testType);

    /**
     * 统计测试总数
     */
    @Query("SELECT COUNT(r) FROM PersonalityTestRecord r")
    Long countTotalTests();

    /**
     * 根据测试类型统计数量
     */
    Long countByTestType(PersonalityTestRecord.TestType testType);

    /**
     * 统计指定时间范围内的测试数量
     */
    @Query("SELECT COUNT(r) FROM PersonalityTestRecord r WHERE r.createdAt BETWEEN :startTime AND :endTime")
    Long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 获取最近的测试记录
     */
    List<PersonalityTestRecord> findTop10ByOrderByCreatedAtDesc();
}
