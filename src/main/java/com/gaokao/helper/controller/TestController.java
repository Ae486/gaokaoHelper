package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.entity.*;
import com.gaokao.helper.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试控制器
 * 用于验证项目是否正常启动
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AdmissionScoreRepository admissionScoreRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private SubjectCategoryRepository subjectCategoryRepository;

    @Autowired
    private ProvincialRankingRepository provincialRankingRepository;

    @Autowired
    private UniversityRankingRepository universityRankingRepository;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "高考志愿填报助手系统运行正常");
        
        return Result.success("系统健康检查通过", data);
    }

    /**
     * 欢迎页面
     */
    @GetMapping("/welcome")
    public Result<String> welcome() {
        return Result.success("欢迎使用高考志愿填报助手系统！");
    }

    /**
     * 根路径重定向
     */
    @GetMapping("/")
    public Result<String> home() {
        return Result.success("高考志愿填报助手系统已启动", "访问 /api/test/health 查看系统状态");
    }

    /**
     * 测试users表访问
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> testUsersTable() {
        try {
            // 获取所有用户
            List<User> users = userRepository.findAll();

            Map<String, Object> data = new HashMap<>();
            data.put("total_users", users.size());
            data.put("users", users);
            data.put("timestamp", LocalDateTime.now());

            return Result.success("成功访问users表", data);
        } catch (Exception e) {
            return Result.error("访问users表失败: " + e.getMessage());
        }
    }

    /**
     * 测试users表连接状态
     */
    @GetMapping("/users/count")
    public Result<Map<String, Object>> testUsersCount() {
        try {
            long count = userRepository.count();

            Map<String, Object> data = new HashMap<>();
            data.put("user_count", count);
            data.put("table_accessible", true);
            data.put("timestamp", LocalDateTime.now());

            return Result.success("users表连接正常", data);
        } catch (Exception e) {
            return Result.error("users表连接失败: " + e.getMessage());
        }
    }

    /**
     * 测试所有表的数据量
     */
    @GetMapping("/tables/count")
    public Result<Map<String, Object>> testAllTablesCount() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 用户表
            data.put("users_count", userRepository.count());

            // 学校表
            data.put("schools_count", schoolRepository.count());

            // 录取分数表
            data.put("admission_scores_count", admissionScoreRepository.count());

            // 省份表
            data.put("provinces_count", provinceRepository.count());

            // 科类表
            data.put("subject_categories_count", subjectCategoryRepository.count());

            // 省份排名表
            data.put("provincial_rankings_count", provincialRankingRepository.count());

            // 大学排名表
            data.put("university_rankings_count", universityRankingRepository.count());

            data.put("timestamp", LocalDateTime.now());

            return Result.success("所有表数据统计", data);
        } catch (Exception e) {
            return Result.error("获取表数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取省份列表
     */
    @GetMapping("/provinces")
    public Result<List<Province>> getProvinces() {
        try {
            List<Province> provinces = provinceRepository.findAll();
            return Result.success("获取省份列表成功", provinces);
        } catch (Exception e) {
            return Result.error("获取省份列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取省份映射表 - 用于前端省份ID到名称的转换
     */
    @GetMapping("/provinces/map")
    public Result<Map<Integer, String>> getProvinceMap() {
        try {
            List<Province> provinces = provinceRepository.findAll();
            Map<Integer, String> provinceMap = provinces.stream()
                .collect(Collectors.toMap(Province::getId, Province::getName));
            return Result.success("获取省份映射成功", provinceMap);
        } catch (Exception e) {
            return Result.error("获取省份映射失败: " + e.getMessage());
        }
    }

    /**
     * 获取科类列表
     */
    @GetMapping("/subject-categories")
    public Result<List<SubjectCategory>> getSubjectCategories() {
        try {
            List<SubjectCategory> categories = subjectCategoryRepository.findAll();
            return Result.success("获取科类列表成功", categories);
        } catch (Exception e) {
            return Result.error("获取科类列表失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据完整性
     */
    @GetMapping("/data/integrity")
    public Result<Map<String, Object>> testDataIntegrity() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 检查学校表中province_id为NULL的记录
            long schoolsWithNullProvince = schoolRepository.countByProvinceIdIsNull();
            data.put("schools_with_null_province", schoolsWithNullProvince);

            // 检查学校表中有效province_id的记录
            long schoolsWithValidProvince = schoolRepository.countByProvinceIdIsNotNull();
            data.put("schools_with_valid_province", schoolsWithValidProvince);

            // 获取前5个学校记录
            List<School> sampleSchools = schoolRepository.findAll().stream().limit(5).collect(Collectors.toList());
            data.put("sample_schools", sampleSchools);

            // 获取前5个录取分数记录
            List<AdmissionScore> sampleScores = admissionScoreRepository.findAll().stream().limit(5).collect(Collectors.toList());
            data.put("sample_admission_scores", sampleScores);

            data.put("timestamp", LocalDateTime.now());

            return Result.success("数据完整性检查", data);
        } catch (Exception e) {
            return Result.error("数据完整性检查失败: " + e.getMessage());
        }
    }

    /**
     * 测试获取学校详细信息
     */
    @GetMapping("/school/{id}")
    public Result<School> getSchoolById(@PathVariable Integer id) {
        try {
            Optional<School> school = schoolRepository.findById(id);
            if (school.isPresent()) {
                return Result.success("获取学校信息成功", school.get());
            } else {
                return Result.error("学校不存在，ID: " + id);
            }
        } catch (Exception e) {
            return Result.error("获取学校信息失败: " + e.getMessage());
        }
    }

    /**
     * 测试获取第一所学校的详细信息
     */
    @GetMapping("/school/first")
    public Result<School> getFirstSchool() {
        try {
            List<School> schools = schoolRepository.findAll();
            if (!schools.isEmpty()) {
                return Result.success("获取第一所学校信息成功", schools.get(0));
            } else {
                return Result.error("没有学校数据");
            }
        } catch (Exception e) {
            return Result.error("获取学校信息失败: " + e.getMessage());
        }
    }

    /**
     * 测试获取前10所学校的详细信息
     */
    @GetMapping("/schools/sample")
    public Result<List<School>> getSampleSchools() {
        try {
            List<School> schools = schoolRepository.findAll().stream().limit(10).collect(Collectors.toList());
            return Result.success("获取示例学校信息成功", schools);
        } catch (Exception e) {
            return Result.error("获取学校信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据学校名称搜索学校信息
     */
    @GetMapping("/school/search/{name}")
    public Result<School> getSchoolByName(@PathVariable String name) {
        try {
            Optional<School> school = schoolRepository.findByName(name);
            if (school.isPresent()) {
                return Result.success("找到学校信息", school.get());
            } else {
                return Result.error("未找到学校: " + name);
            }
        } catch (Exception e) {
            return Result.error("搜索学校失败: " + e.getMessage());
        }
    }

    /**
     * 模糊搜索学校名称
     */
    @GetMapping("/schools/search/{keyword}")
    public Result<List<School>> searchSchoolsByKeyword(@PathVariable String keyword) {
        try {
            List<School> schools = schoolRepository.findAll().stream()
                    .filter(school -> school.getName().contains(keyword))
                    .limit(10)
                    .collect(Collectors.toList());
            return Result.success("搜索结果", schools);
        } catch (Exception e) {
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
}
