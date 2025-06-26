package com.gaokao.helper.service.impl;

import com.gaokao.helper.common.BusinessException;
import com.gaokao.helper.common.PageResult;
import com.gaokao.helper.entity.*;
import com.gaokao.helper.repository.*;
import com.gaokao.helper.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * 管理员服务实现类
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final AdmissionScoreRepository admissionScoreRepository;
    private final ProvinceRepository provinceRepository;
    private final AdminLogRepository adminLogRepository;

    private static final String ADMIN_USERNAME = "PLeiA";

    @Override
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return ADMIN_USERNAME.equals(authentication.getName());
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各表数据量
        stats.put("userCount", userRepository.count());
        stats.put("schoolCount", schoolRepository.count());
        stats.put("admissionScoreCount", admissionScoreRepository.count());
        stats.put("provinceCount", provinceRepository.count());
        
        // 统计操作日志
        stats.put("totalOperations", adminLogRepository.count());
        stats.put("adminOperations", adminLogRepository.countByAdminUsername(ADMIN_USERNAME));
        
        // 统计各种操作类型
        stats.put("createOperations", adminLogRepository.countByOperationType("CREATE"));
        stats.put("updateOperations", adminLogRepository.countByOperationType("UPDATE"));
        stats.put("deleteOperations", adminLogRepository.countByOperationType("DELETE"));
        stats.put("viewOperations", adminLogRepository.countByOperationType("VIEW"));
        
        return stats;
    }

    // ==================== 用户管理 ====================

    @Override
    public PageResult<User> getUserList(String keyword, Pageable pageable) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.like(root.get("username"), "%" + keyword + "%");
            }
            return null;
        };
        
        Page<User> page = userRepository.findAll(spec, pageable);
        return PageResult.of(page);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        // 只允许更新用户名（如果提供了新用户名且不同）
        if (StringUtils.hasText(user.getUsername()) && 
            !existingUser.getUsername().equals(user.getUsername())) {
            
            // 检查新用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                throw BusinessException.badRequest("用户名已存在");
            }
            
            existingUser.setUsername(user.getUsername());
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // 不允许删除管理员账户
        if (ADMIN_USERNAME.equals(user.getUsername())) {
            throw BusinessException.badRequest("不能删除管理员账户");
        }
        
        userRepository.deleteById(id);
        log.info("管理员删除用户: id={}, username={}", id, user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUsers(List<Long> ids) {
        for (Long id : ids) {
            deleteUser(id);
        }
    }

    // ==================== 学校管理 ====================

    @Override
    public PageResult<School> getSchoolList(String keyword, Pageable pageable) {
        Specification<School> spec = (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
            }
            return null;
        };
        
        Page<School> page = schoolRepository.findAll(spec, pageable);
        return PageResult.of(page);
    }

    @Override
    public School getSchoolById(Integer id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("学校不存在"));
    }

    @Override
    @Transactional
    public School createSchool(School school) {
        // 检查学校名称是否已存在
        if (schoolRepository.existsByName(school.getName())) {
            throw BusinessException.badRequest("学校名称已存在");
        }
        
        return schoolRepository.save(school);
    }

    @Override
    @Transactional
    public School updateSchool(Integer id, School school) {
        School existingSchool = getSchoolById(id);
        
        // 更新学校信息
        if (StringUtils.hasText(school.getName())) {
            existingSchool.setName(school.getName());
        }
        if (school.getProvinceId() != null) {
            existingSchool.setProvinceId(school.getProvinceId());
        }
        if (StringUtils.hasText(school.getSchoolLevel())) {
            existingSchool.setSchoolLevel(school.getSchoolLevel());
        }
        if (StringUtils.hasText(school.getSchoolAffiliation())) {
            existingSchool.setSchoolAffiliation(school.getSchoolAffiliation());
        }
        if (StringUtils.hasText(school.getOwnershipType())) {
            existingSchool.setOwnershipType(school.getOwnershipType());
        }
        if (StringUtils.hasText(school.getSchoolType())) {
            existingSchool.setSchoolType(school.getSchoolType());
        }
        if (StringUtils.hasText(school.getTags())) {
            existingSchool.setTags(school.getTags());
        }
        if (StringUtils.hasText(school.getSchoolIntroduction())) {
            existingSchool.setSchoolIntroduction(school.getSchoolIntroduction());
        }
        if (StringUtils.hasText(school.getSchoolNationalProfessions())) {
            existingSchool.setSchoolNationalProfessions(school.getSchoolNationalProfessions());
        }
        
        return schoolRepository.save(existingSchool);
    }

    @Override
    @Transactional
    public void deleteSchool(Integer id) {
        School school = getSchoolById(id);
        schoolRepository.deleteById(id);
        log.info("管理员删除学校: id={}, name={}", id, school.getName());
    }

    @Override
    @Transactional
    public void deleteSchools(List<Integer> ids) {
        for (Integer id : ids) {
            deleteSchool(id);
        }
    }

    // ==================== 录取分数管理 ====================

    @Override
    public PageResult<AdmissionScore> getAdmissionScoreList(Integer year, Integer schoolId, 
                                                           Integer provinceId, Pageable pageable) {
        Specification<AdmissionScore> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), year));
            }
            if (schoolId != null) {
                predicates.add(criteriaBuilder.equal(root.get("schoolId"), schoolId));
            }
            if (provinceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("provinceId"), provinceId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<AdmissionScore> page = admissionScoreRepository.findAll(spec, pageable);
        return PageResult.of(page);
    }

    @Override
    public AdmissionScore getAdmissionScoreById(Long id) {
        return admissionScoreRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("录取分数记录不存在"));
    }

    @Override
    @Transactional
    public AdmissionScore createAdmissionScore(AdmissionScore admissionScore) {
        return admissionScoreRepository.save(admissionScore);
    }

    @Override
    @Transactional
    public AdmissionScore updateAdmissionScore(Long id, AdmissionScore admissionScore) {
        AdmissionScore existing = getAdmissionScoreById(id);
        
        if (admissionScore.getYear() != null) {
            existing.setYear(admissionScore.getYear());
        }
        if (admissionScore.getSchoolId() != null) {
            existing.setSchoolId(admissionScore.getSchoolId());
        }
        if (admissionScore.getProvinceId() != null) {
            existing.setProvinceId(admissionScore.getProvinceId());
        }
        if (admissionScore.getSubjectCategoryId() != null) {
            existing.setSubjectCategoryId(admissionScore.getSubjectCategoryId());
        }
        if (admissionScore.getMinScore() != null) {
            existing.setMinScore(admissionScore.getMinScore());
        }
        if (admissionScore.getMinRank() != null) {
            existing.setMinRank(admissionScore.getMinRank());
        }
        
        return admissionScoreRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAdmissionScore(Long id) {
        AdmissionScore score = getAdmissionScoreById(id);
        admissionScoreRepository.deleteById(id);
        log.info("管理员删除录取分数记录: id={}", id);
    }

    @Override
    @Transactional
    public void deleteAdmissionScores(List<Long> ids) {
        for (Long id : ids) {
            deleteAdmissionScore(id);
        }
    }

    // ==================== 省份管理 ====================

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public Province getProvinceById(Integer id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("省份不存在"));
    }

    @Override
    @Transactional
    public Province createProvince(Province province) {
        return provinceRepository.save(province);
    }

    @Override
    @Transactional
    public Province updateProvince(Integer id, Province province) {
        Province existing = getProvinceById(id);
        
        if (StringUtils.hasText(province.getName())) {
            existing.setName(province.getName());
        }
        
        return provinceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProvince(Integer id) {
        Province province = getProvinceById(id);
        provinceRepository.deleteById(id);
        log.info("管理员删除省份: id={}, name={}", id, province.getName());
    }

    // ==================== 操作日志管理 ====================

    @Override
    public PageResult<AdminLog> getAdminLogList(String adminUsername, String operationType, 
                                               String tableName, Pageable pageable) {
        Specification<AdminLog> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(adminUsername)) {
                predicates.add(criteriaBuilder.equal(root.get("adminUsername"), adminUsername));
            }
            if (StringUtils.hasText(operationType)) {
                predicates.add(criteriaBuilder.equal(root.get("operationType"), operationType));
            }
            if (StringUtils.hasText(tableName)) {
                predicates.add(criteriaBuilder.equal(root.get("tableName"), tableName));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<AdminLog> page = adminLogRepository.findAll(spec, pageable);
        return PageResult.of(page);
    }

    @Override
    public List<AdminLog> getRecentAdminLogs() {
        return adminLogRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public Map<String, Object> getOperationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalOperations", adminLogRepository.count());
        stats.put("createCount", adminLogRepository.countByOperationType("CREATE"));
        stats.put("updateCount", adminLogRepository.countByOperationType("UPDATE"));
        stats.put("deleteCount", adminLogRepository.countByOperationType("DELETE"));
        stats.put("viewCount", adminLogRepository.countByOperationType("VIEW"));
        
        return stats;
    }
}
