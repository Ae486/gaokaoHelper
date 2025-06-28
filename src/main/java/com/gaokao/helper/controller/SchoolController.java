package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import com.gaokao.helper.entity.School;
import com.gaokao.helper.repository.SchoolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 院校查询控制器
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "院校查询", description = "院校信息查询相关接口")
public class SchoolController {

    private final SchoolRepository schoolRepository;

    /**
     * 根据ID获取学校详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取学校详细信息", description = "根据学校ID获取完整的学校信息")
    public Result<School> getSchoolById(@Parameter(description = "学校ID") @PathVariable Integer id) {
        try {
            log.info("获取学校详细信息: schoolId={}", id);

            Optional<School> schoolOpt = schoolRepository.findById(id);
            if (schoolOpt.isPresent()) {
                School school = schoolOpt.get();
                log.info("成功获取学校信息: {}", school.getName());
                return Result.success("获取学校详细信息成功", school);
            } else {
                log.warn("学校不存在: schoolId={}", id);
                return Result.error("学校不存在，ID: " + id);
            }
        } catch (Exception e) {
            log.error("获取学校详细信息失败: schoolId={}", id, e);
            return Result.error("获取学校详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据名称搜索学校
     */
    @GetMapping("/search")
    @Operation(summary = "搜索学校", description = "根据学校名称搜索学校信息")
    public Result<School> searchSchoolByName(@Parameter(description = "学校名称") @RequestParam String name) {
        try {
            log.info("搜索学校: name={}", name);

            Optional<School> schoolOpt = schoolRepository.findByName(name);
            if (schoolOpt.isPresent()) {
                School school = schoolOpt.get();
                log.info("成功找到学校: {}", school.getName());
                return Result.success("搜索学校成功", school);
            } else {
                log.warn("未找到学校: name={}", name);
                return Result.error("未找到学校: " + name);
            }
        } catch (Exception e) {
            log.error("搜索学校失败: name={}", name, e);
            return Result.error("搜索学校失败: " + e.getMessage());
        }
    }
}
