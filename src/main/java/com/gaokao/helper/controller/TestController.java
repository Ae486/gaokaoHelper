package com.gaokao.helper.controller;

import com.gaokao.helper.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
}
