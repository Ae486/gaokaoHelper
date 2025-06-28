package com.gaokao.helper.repository;

import com.gaokao.helper.entity.MbtiQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MBTI测试题Repository
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Repository
public interface MbtiQuestionRepository extends JpaRepository<MbtiQuestion, Integer> {

    /**
     * 按题目顺序获取所有题目
     */
    List<MbtiQuestion> findAllByOrderByQuestionOrderAsc();

    /**
     * 根据维度获取题目
     */
    List<MbtiQuestion> findByDimensionOrderByQuestionOrderAsc(MbtiQuestion.MbtiDimension dimension);

    /**
     * 获取题目总数
     */
    @Query("SELECT COUNT(q) FROM MbtiQuestion q")
    Long countTotalQuestions();

    /**
     * 根据维度统计题目数量
     */
    Long countByDimension(MbtiQuestion.MbtiDimension dimension);
}
