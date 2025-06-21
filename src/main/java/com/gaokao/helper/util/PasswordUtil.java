package com.gaokao.helper.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

/**
 * 密码工具类
 *
 * @author PLeiA
 * @since 2024-06-20
 */
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // 密码强度正则表达式：至少8位，包含大小写字母和数字
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 是否符合强度要求
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    /**
     * 获取密码强度要求说明
     *
     * @return 密码强度要求说明
     */
    public static String getPasswordRequirements() {
        return "密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字";
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8;
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@$!%*?&";
        String allChars = upperCase + lowerCase + digits + specialChars;

        StringBuilder password = new StringBuilder();

        // 确保至少包含一个大写字母、小写字母和数字
        password.append(upperCase.charAt((int) (Math.random() * upperCase.length())));
        password.append(lowerCase.charAt((int) (Math.random() * lowerCase.length())));
        password.append(digits.charAt((int) (Math.random() * digits.length())));

        // 填充剩余长度
        for (int i = 3; i < length; i++) {
            password.append(allChars.charAt((int) (Math.random() * allChars.length())));
        }

        // 打乱字符顺序
        return shuffleString(password.toString());
    }

    /**
     * 打乱字符串顺序
     *
     * @param input 输入字符串
     * @return 打乱后的字符串
     */
    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
