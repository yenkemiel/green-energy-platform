package com.kemiel.greenenergy.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具類，負責 Token 的簽發、解析與有效性驗證
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 應用程式啟動時驗證 JWT Secret 長度，不足 32 字元時中止啟動
     */
    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "jwt.secret 至少需要 32 個字元（256 bits），請檢查 application.yml"
            );
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 簽發 JWT Token，Payload 包含 userId、username、role
     */
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT Token 並回傳 Payload，呼叫前請先去除 Bearer 前綴。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 驗證 JWT Token 是否有效，過期、格式錯誤或簽名驗證失敗皆回傳 false
     */
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已過期：{}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.warn("JWT 格式錯誤：{}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("JWT 簽名驗證失敗：{}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT 為 null 或空字串：{}", e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }
}