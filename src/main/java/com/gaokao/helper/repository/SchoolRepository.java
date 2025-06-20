package com.gaokao.helper.repository;

import com.gaokao.helper.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 院校数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {

    /**
     * 根据学校名称查找学校
     * 
     * @param name 学校名称
     * @return 学校信息
     */
    Optional<School> findByName(String name);

    /**
     * 检查学校名称是否存在
     * 
     * @param name 学校名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据省份ID查找学校列表
     * 
     * @param provinceId 省份ID
     * @return 学校列表
     */
    List<School> findByProvinceId(Integer provinceId);

    /**
     * 根据院校类型查找学校列表
     * 
     * @param schoolType 院校类型
     * @return 学校列表
     */
    List<School> findBySchoolType(String schoolType);

    /**
     * 根据办学层次查找学校列表
     * 
     * @param schoolLevel 办学层次
     * @return 学校列表
     */
    List<School> findBySchoolLevel(String schoolLevel);

    /**
     * 根据办学性质查找学校列表
     * 
     * @param ownershipType 办学性质
     * @return 学校列表
     */
    List<School> findByOwnershipType(String ownershipType);

    /**
     * 根据学校名称模糊查询（分页）
     * 
     * @param name 学校名称关键字
     * @param pageable 分页参数
     * @return 学校分页列表
     */
    Page<School> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 根据标签查找学校（包含指定标签的学校）
     * 
     * @param tag 标签
     * @return 学校列表
     */
    @Query("SELECT s FROM School s WHERE s.tags LIKE %:tag%")
    List<School> findByTagsContaining(@Param("tag") String tag);
}
