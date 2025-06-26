package com.gaokao.helper.controller;

import com.gaokao.helper.annotation.AdminRequired;
import com.gaokao.helper.common.PageResult;
import com.gaokao.helper.common.Result;
import com.gaokao.helper.entity.*;
import com.gaokao.helper.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供管理后台的所有功能接口
 * 
 * @author PLeiA
 * @since 2024-06-25
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员接口", description = "管理后台相关接口")
public class AdminController {

    private final AdminService adminService;

    /**
     * 检查管理员权限
     */
    @GetMapping("/check")
    @Operation(summary = "检查管理员权限", description = "检查当前用户是否具有管理员权限")
    public Result<Map<String, Object>> checkAdminPermission() {
        boolean isAdmin = adminService.isCurrentUserAdmin();
        Map<String, Object> data = Map.of("isAdmin", isAdmin);
        return Result.success("权限检查完成", data);
    }

    /**
     * 获取管理后台统计信息
     */
    @GetMapping("/dashboard")
    @AdminRequired("获取管理后台统计信息")
    @Operation(summary = "获取管理后台统计信息", description = "获取各种数据统计信息")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return Result.success("获取统计信息成功", stats);
    }

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    @AdminRequired("查询用户列表")
    @Operation(summary = "分页查询用户列表", description = "支持关键字搜索的用户列表查询")
    public Result<PageResult<User>> getUserList(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        PageResult<User> result = adminService.getUserList(keyword, pageable);
        return Result.success("查询用户列表成功", result);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/users/{id}")
    @AdminRequired("查询用户详情")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取详细信息")
    public Result<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = adminService.getUserById(id);
        return Result.success("获取用户详情成功", user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    @AdminRequired("更新用户信息")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    public Result<User> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody User user) {
        User updatedUser = adminService.updateUser(id, user);
        return Result.success("更新用户信息成功", updatedUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    @AdminRequired("删除用户")
    @Operation(summary = "删除用户", description = "删除指定的用户")
    public Result<String> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/users/batch")
    @AdminRequired("批量删除用户")
    @Operation(summary = "批量删除用户", description = "批量删除多个用户")
    public Result<String> deleteUsers(@RequestBody List<Long> ids) {
        adminService.deleteUsers(ids);
        return Result.success("批量删除用户成功");
    }

    // ==================== 学校管理 ====================

    /**
     * 分页查询学校列表
     */
    @GetMapping("/schools")
    @AdminRequired("查询学校列表")
    @Operation(summary = "分页查询学校列表", description = "支持关键字搜索的学校列表查询")
    public Result<PageResult<School>> getSchoolList(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        PageResult<School> result = adminService.getSchoolList(keyword, pageable);
        return Result.success("查询学校列表成功", result);
    }

    /**
     * 根据ID获取学校详情
     */
    @GetMapping("/schools/{id}")
    @AdminRequired("查询学校详情")
    @Operation(summary = "获取学校详情", description = "根据学校ID获取详细信息")
    public Result<School> getSchoolById(@Parameter(description = "学校ID") @PathVariable Integer id) {
        School school = adminService.getSchoolById(id);
        return Result.success("获取学校详情成功", school);
    }

    /**
     * 创建学校
     */
    @PostMapping("/schools")
    @AdminRequired("创建学校")
    @Operation(summary = "创建学校", description = "创建新的学校记录")
    public Result<School> createSchool(@Valid @RequestBody School school) {
        School createdSchool = adminService.createSchool(school);
        return Result.success("创建学校成功", createdSchool);
    }

    /**
     * 更新学校信息
     */
    @PutMapping("/schools/{id}")
    @AdminRequired("更新学校信息")
    @Operation(summary = "更新学校信息", description = "更新指定学校的信息")
    public Result<School> updateSchool(
            @Parameter(description = "学校ID") @PathVariable Integer id,
            @Valid @RequestBody School school) {
        School updatedSchool = adminService.updateSchool(id, school);
        return Result.success("更新学校信息成功", updatedSchool);
    }

    /**
     * 删除学校
     */
    @DeleteMapping("/schools/{id}")
    @AdminRequired("删除学校")
    @Operation(summary = "删除学校", description = "删除指定的学校")
    public Result<String> deleteSchool(@Parameter(description = "学校ID") @PathVariable Integer id) {
        adminService.deleteSchool(id);
        return Result.success("删除学校成功");
    }

    /**
     * 批量删除学校
     */
    @DeleteMapping("/schools/batch")
    @AdminRequired("批量删除学校")
    @Operation(summary = "批量删除学校", description = "批量删除多个学校")
    public Result<String> deleteSchools(@RequestBody List<Integer> ids) {
        adminService.deleteSchools(ids);
        return Result.success("批量删除学校成功");
    }

    // ==================== 录取分数管理 ====================

    /**
     * 分页查询录取分数列表
     */
    @GetMapping("/admission-scores")
    @AdminRequired("查询录取分数列表")
    @Operation(summary = "分页查询录取分数列表", description = "支持多条件筛选的录取分数查询")
    public Result<PageResult<AdmissionScore>> getAdmissionScoreList(
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "学校ID") @RequestParam(required = false) Integer schoolId,
            @Parameter(description = "省份ID") @RequestParam(required = false) Integer provinceId,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        PageResult<AdmissionScore> result = adminService.getAdmissionScoreList(year, schoolId, provinceId, pageable);
        return Result.success("查询录取分数列表成功", result);
    }

    /**
     * 根据ID获取录取分数详情
     */
    @GetMapping("/admission-scores/{id}")
    @AdminRequired("查询录取分数详情")
    @Operation(summary = "获取录取分数详情", description = "根据ID获取录取分数详细信息")
    public Result<AdmissionScore> getAdmissionScoreById(@Parameter(description = "录取分数ID") @PathVariable Long id) {
        AdmissionScore score = adminService.getAdmissionScoreById(id);
        return Result.success("获取录取分数详情成功", score);
    }

    /**
     * 创建录取分数记录
     */
    @PostMapping("/admission-scores")
    @AdminRequired("创建录取分数记录")
    @Operation(summary = "创建录取分数记录", description = "创建新的录取分数记录")
    public Result<AdmissionScore> createAdmissionScore(@Valid @RequestBody AdmissionScore admissionScore) {
        AdmissionScore created = adminService.createAdmissionScore(admissionScore);
        return Result.success("创建录取分数记录成功", created);
    }

    /**
     * 更新录取分数记录
     */
    @PutMapping("/admission-scores/{id}")
    @AdminRequired("更新录取分数记录")
    @Operation(summary = "更新录取分数记录", description = "更新指定的录取分数记录")
    public Result<AdmissionScore> updateAdmissionScore(
            @Parameter(description = "录取分数ID") @PathVariable Long id,
            @Valid @RequestBody AdmissionScore admissionScore) {
        AdmissionScore updated = adminService.updateAdmissionScore(id, admissionScore);
        return Result.success("更新录取分数记录成功", updated);
    }

    /**
     * 删除录取分数记录
     */
    @DeleteMapping("/admission-scores/{id}")
    @AdminRequired("删除录取分数记录")
    @Operation(summary = "删除录取分数记录", description = "删除指定的录取分数记录")
    public Result<String> deleteAdmissionScore(@Parameter(description = "录取分数ID") @PathVariable Long id) {
        adminService.deleteAdmissionScore(id);
        return Result.success("删除录取分数记录成功");
    }

    /**
     * 批量删除录取分数记录
     */
    @DeleteMapping("/admission-scores/batch")
    @AdminRequired("批量删除录取分数记录")
    @Operation(summary = "批量删除录取分数记录", description = "批量删除多个录取分数记录")
    public Result<String> deleteAdmissionScores(@RequestBody List<Long> ids) {
        adminService.deleteAdmissionScores(ids);
        return Result.success("批量删除录取分数记录成功");
    }

    // ==================== 省份管理 ====================

    /**
     * 获取所有省份列表
     */
    @GetMapping("/provinces")
    @AdminRequired("查询省份列表")
    @Operation(summary = "获取所有省份列表", description = "获取系统中所有省份信息")
    public Result<List<Province>> getAllProvinces() {
        List<Province> provinces = adminService.getAllProvinces();
        return Result.success("获取省份列表成功", provinces);
    }

    /**
     * 根据ID获取省份详情
     */
    @GetMapping("/provinces/{id}")
    @AdminRequired("查询省份详情")
    @Operation(summary = "获取省份详情", description = "根据省份ID获取详细信息")
    public Result<Province> getProvinceById(@Parameter(description = "省份ID") @PathVariable Integer id) {
        Province province = adminService.getProvinceById(id);
        return Result.success("获取省份详情成功", province);
    }

    /**
     * 创建省份
     */
    @PostMapping("/provinces")
    @AdminRequired("创建省份")
    @Operation(summary = "创建省份", description = "创建新的省份记录")
    public Result<Province> createProvince(@Valid @RequestBody Province province) {
        Province created = adminService.createProvince(province);
        return Result.success("创建省份成功", created);
    }

    /**
     * 更新省份信息
     */
    @PutMapping("/provinces/{id}")
    @AdminRequired("更新省份信息")
    @Operation(summary = "更新省份信息", description = "更新指定省份的信息")
    public Result<Province> updateProvince(
            @Parameter(description = "省份ID") @PathVariable Integer id,
            @Valid @RequestBody Province province) {
        Province updated = adminService.updateProvince(id, province);
        return Result.success("更新省份信息成功", updated);
    }

    /**
     * 删除省份
     */
    @DeleteMapping("/provinces/{id}")
    @AdminRequired("删除省份")
    @Operation(summary = "删除省份", description = "删除指定的省份")
    public Result<String> deleteProvince(@Parameter(description = "省份ID") @PathVariable Integer id) {
        adminService.deleteProvince(id);
        return Result.success("删除省份成功");
    }

    // ==================== 操作日志管理 ====================

    /**
     * 分页查询操作日志
     */
    @GetMapping("/logs")
    @AdminRequired("查询操作日志")
    @Operation(summary = "分页查询操作日志", description = "支持多条件筛选的操作日志查询")
    public Result<PageResult<AdminLog>> getAdminLogList(
            @Parameter(description = "管理员用户名") @RequestParam(required = false) String adminUsername,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "表名") @RequestParam(required = false) String tableName,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        PageResult<AdminLog> result = adminService.getAdminLogList(adminUsername, operationType, tableName, pageable);
        return Result.success("查询操作日志成功", result);
    }

    /**
     * 获取最近的操作日志
     */
    @GetMapping("/logs/recent")
    @AdminRequired("查询最近操作日志")
    @Operation(summary = "获取最近的操作日志", description = "获取最近10条操作日志")
    public Result<List<AdminLog>> getRecentAdminLogs() {
        List<AdminLog> logs = adminService.getRecentAdminLogs();
        return Result.success("获取最近操作日志成功", logs);
    }

    /**
     * 获取操作统计信息
     */
    @GetMapping("/logs/stats")
    @AdminRequired("查询操作统计")
    @Operation(summary = "获取操作统计信息", description = "获取各种操作类型的统计数据")
    public Result<Map<String, Object>> getOperationStats() {
        Map<String, Object> stats = adminService.getOperationStats();
        return Result.success("获取操作统计成功", stats);
    }
}
