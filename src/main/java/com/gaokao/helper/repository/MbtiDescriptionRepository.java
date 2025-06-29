package com.gaokao.helper.repository;

import com.gaokao.helper.entity.MbtiDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MBTI类型描述Repository
 * 
 * @author PLeiA
 * @since 2024-06-29
 */
@Repository
public interface MbtiDescriptionRepository extends JpaRepository<MbtiDescription, Integer> {

    /**
     * 根据MBTI类型查找描述信息
     */
    Optional<MbtiDescription> findByMbtiType(String mbtiType);

    /**
     * 检查MBTI类型是否存在
     */
    boolean existsByMbtiType(String mbtiType);
}
