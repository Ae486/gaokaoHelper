package com.gaokao.helper.repository;

import com.gaokao.helper.entity.HollandQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 霍兰德测试题Repository
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Repository
public interface HollandQuestionRepository extends JpaRepository<HollandQuestion, Integer> {

    /**
     * 按题目顺序获取所有题目
     */
    List<HollandQuestion> findAllByOrderByQuestionOrderAsc();

    /**
     * 根据维度获取题目
     */
    List<HollandQuestion> findByDimensionOrderByQuestionOrderAsc(HollandQuestion.HollandDimension dimension);

    /**
     * 根据题目类型获取题目
     */
    List<HollandQuestion> findByQuestionTypeOrderByQuestionOrderAsc(HollandQuestion.QuestionType questionType);

    /**
     * 获取题目总数
     */
    @Query("SELECT COUNT(q) FROM HollandQuestion q")
    Long countTotalQuestions();

    /**
     * 根据维度统计题目数量
     */
    Long countByDimension(HollandQuestion.HollandDimension dimension);

    /**
     * 根据题目类型统计数量
     */
    Long countByQuestionType(HollandQuestion.QuestionType questionType);
}
