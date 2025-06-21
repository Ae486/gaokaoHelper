package com.gaokao.helper.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author PLeiA
 * @since 2024-06-20
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    /**
     * 生成JWT密钥
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从token中获取用户名
     *
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取过期时间
     *
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取Claims
     *
     * @param token JWT token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证token是否过期
     *
     * @param token JWT token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从请求头中获取token
     *
     * @param request HTTP请求
     * @return JWT token
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            return getUserIdFromToken(token);
        }
        return null;
    }

    /**
     * 从请求中获取用户名
     *
     * @param request HTTP请求
     * @return 用户名
     */
    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            return getUsernameFromToken(token);
        }
        return null;
    }
}
