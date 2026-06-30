package com.portfolio.api.session;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis 기반 세션 관리자 — 중복 로그인 차단 핵심 컴포넌트
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │  Redis 키 구조                                            │
 * │                                                          │
 * │  session:user:{userId}  →  {token}        (사용자당 1개)  │
 * │  session:token:{token}  →  {userId}       (토큰당 1개)   │
 * │                                                          │
 * │  두 키 모두 동일한 TTL로 만료됩니다.                         │
 * └─────────────────────────────────────────────────────────┘
 *
 * 중복 로그인 처리 흐름:
 *   1. A 기기에서 로그인 → session:user:admin = tokenA 저장
 *   2. B 기기에서 로그인 시도
 *      → session:user:admin 에 기존 tokenA 존재 확인
 *      → tokenA 즉시 삭제 (A 기기 강제 로그아웃)
 *      → 새 tokenB 발급 후 저장
 */
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${session.ttl-seconds:3600}")
    private long ttlSeconds;

    private static final String USER_KEY_PREFIX  = "session:user:";
    private static final String TOKEN_KEY_PREFIX = "session:token:";

    /**
     * 새 세션을 생성합니다.
     * 동일 userId의 기존 세션이 있으면 먼저 강제 만료 후 새 세션을 발급합니다.
     *
     * @param userId 로그인한 사용자 ID
     * @return 새로 발급된 세션 토큰
     */
    public String createSession(String userId) {
        String userKey = USER_KEY_PREFIX + userId;

        // 기존 세션 확인 → 있으면 중복 로그인으로 판단하여 강제 만료
        String existingToken = redisTemplate.opsForValue().get(userKey);
        if (existingToken != null) {
            redisTemplate.delete(TOKEN_KEY_PREFIX + existingToken);
        }

        // 새 토큰 발급
        String newToken = UUID.randomUUID().toString();
        Duration ttl = Duration.ofSeconds(ttlSeconds);

        redisTemplate.opsForValue().set(userKey, newToken, ttl);
        redisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + newToken, userId, ttl);

        return newToken;
    }

    /**
     * 토큰이 유효한지 확인합니다.
     *
     * @return 유효하면 userId, 만료됐거나 없으면 null
     */
    public String validateToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
    }

    /**
     * 세션을 즉시 무효화합니다 (로그아웃).
     */
    public void invalidateSession(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        if (userId != null) {
            redisTemplate.delete(USER_KEY_PREFIX + userId);
        }
        redisTemplate.delete(TOKEN_KEY_PREFIX + token);
    }

    /**
     * 세션의 TTL을 갱신합니다 (활성 사용자 세션 유지).
     */
    public void refreshSession(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        if (userId == null) {
            return;
        }
        Duration ttl = Duration.ofSeconds(ttlSeconds);
        redisTemplate.expire(TOKEN_KEY_PREFIX + token, ttl);
        redisTemplate.expire(USER_KEY_PREFIX + userId, ttl);
    }
}
