package com.gaokao.helper.service;

import com.gaokao.helper.common.PageResult;
import com.gaokao.helper.entity.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
public interface AdminService {

    /**
     * 检查当前用户是否为管理员
     */
    boolean isCurrentUserAdmin();

    /**
     * 获取管理后台统计信息
     */
    Map<String, Object> getDashboardStats();

    // ==================== 用户管理 ====================
    
    /**
     * 分页查询用户列表
     */
    PageResult<User> getUserList(String keyword, Pageable pageable);

    /**
     * 根据ID获取用户详情
     */
    User getUserById(Long id);

    /**
     * 更新用户信息
     */
    User updateUser(Long id, User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     */
    void deleteUsers(List<Long> ids);

    // ==================== 学校管理 ====================
    
    /**
     * 分页查询学校列表
     */
    PageResult<School> getSchoolList(String keyword, Pageable pageable);

    /**
     * 根据ID获取学校详情
     */
    School getSchoolById(Integer id);

    /**
     * 创建学校
     */
    School createSchool(School school);

    /**
     * 更新学校信息
     */
    School updateSchool(Integer id, School school);

    /**
     * 删除学校
     */
    void deleteSchool(Integer id);

    /**
     * 批量删除学校
     */
    void deleteSchools(List<Integer> ids);

    // ==================== 录取分数管理 ====================
    
    /**
     * 分页查询录取分数列表
     */
    PageResult<AdmissionScore> getAdmissionScoreList(Integer year, Integer schoolId, 
                                                    Integer provinceId, Pageable pageable);

    /**
     * 根据ID获取录取分数详情
     */
    AdmissionScore getAdmissionScoreById(Long id);

    /**
     * 创建录取分数记录
     */
    AdmissionScore createAdmissionScore(AdmissionScore admissionScore);

    /**
     * 更新录取分数记录
     */
    AdmissionScore updateAdmissionScore(Long id, AdmissionScore admissionScore);

    /**
     * 删除录取分数记录
     */
    void deleteAdmissionScore(Long id);

    /**
     * 批量删除录取分数记录
     */
    void deleteAdmissionScores(List<Long> ids);

    // ==================== 省份管理 ====================
    
    /**
     * 获取所有省份列表
     */
    List<Province> getAllProvinces();

    /**
     * 根据ID获取省份详情
     */
    Province getProvinceById(Integer id);

    /**
     * 创建省份
     */
    Province createProvince(Province province);

    /**
     * 更新省份信息
     */
    Province updateProvince(Integer id, Province province);

    /**
     * 删除省份
     */
    void deleteProvince(Integer id);

    // ==================== 操作日志管理 ====================
    
    /**
     * 分页查询操作日志
     */
    PageResult<AdminLog> getAdminLogList(String adminUsername, String operationType, 
                                        String tableName, Pageable pageable);

    /**
     * 获取最近的操作日志
     */
    List<AdminLog> getRecentAdminLogs();

    /**
     * 获取操作统计信息
     */
    Map<String, Object> getOperationStats();
}
