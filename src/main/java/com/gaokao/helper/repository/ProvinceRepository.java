package com.gaokao.helper.repository;

import com.gaokao.helper.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 省份数据访问接口
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    /**
     * 根据省份名称查找省份
     * 
     * @param name 省份名称
     * @return 省份信息
     */
    Optional<Province> findByName(String name);

    /**
     * 检查省份名称是否存在
     * 
     * @param name 省份名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}
