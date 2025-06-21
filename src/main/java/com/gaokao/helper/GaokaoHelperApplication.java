package com.gaokao.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 高考志愿填报助手系统启动类
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
// @EnableCaching  // 暂时禁用缓存，避免Redis连接问题
@EnableAsync
public class GaokaoHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaokaoHelperApplication.class, args);
        System.out.println("=================================");
        System.out.println("高考志愿填报助手系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================");
    }
}
