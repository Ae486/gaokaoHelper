package com.gaokao.helper.repository;

import com.gaokao.helper.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 专业信息Repository
 * 
 * @author PLeiA
 * @since 2024-06-28
 */
@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {

    /**
     * 根据专业名称查找
     */
    Optional<Major> findByMajorName(String majorName);

    /**
     * 根据专业代码查找
     */
    Optional<Major> findByMajorCode(String majorCode);

    /**
     * 根据专业类别查找
     */
    List<Major> findByCategory(String category);

    /**
     * 根据专业名称模糊查询
     */
    @Query("SELECT m FROM Major m WHERE m.majorName LIKE %:keyword%")
    List<Major> findByMajorNameContaining(@Param("keyword") String keyword);

    /**
     * 获取所有专业类别
     */
    @Query("SELECT DISTINCT m.category FROM Major m ORDER BY m.category")
    List<String> findAllCategories();

    /**
     * 根据ID列表获取专业信息
     */
    @Query("SELECT m FROM Major m WHERE m.id IN :ids")
    List<Major> findByIdIn(@Param("ids") List<Integer> ids);
}
