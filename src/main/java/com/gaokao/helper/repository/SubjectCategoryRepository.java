package com.gaokao.helper.repository;

import com.gaokao.helper.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 科类数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Integer> {

    /**
     * 根据科类名称查找科类
     * 
     * @param name 科类名称
     * @return 科类信息
     */
    Optional<SubjectCategory> findByName(String name);

    /**
     * 检查科类名称是否存在
     * 
     * @param name 科类名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}
