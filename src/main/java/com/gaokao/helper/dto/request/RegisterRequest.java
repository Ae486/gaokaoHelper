package com.gaokao.helper.dto.request;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 用户注册请求DTO
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
public class RegisterRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$", 
             message = "密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号（可选）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 科类ID
     */
    private Integer subjectTypeId;

    /**
     * 高考年份
     */
    @Min(value = 2020, message = "高考年份不能早于2020年")
    @Max(value = 2030, message = "高考年份不能晚于2030年")
    private Integer examYear;

    /**
     * 高考总分
     */
    @Min(value = 0, message = "高考总分不能为负数")
    @Max(value = 750, message = "高考总分不能超过750分")
    private Integer totalScore;
}
