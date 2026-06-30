package com.portfolio.api.controller;

import com.portfolio.api.dto.PortfolioResponse;
import com.portfolio.api.service.PortfolioService;
import com.portfolio.api.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final SessionManager sessionManager;

    /**
     * [공개] 포트폴리오 전체 데이터 반환
     * Frontend(React)가 이 엔드포인트를 호출하여 동적 데이터를 표시합니다.
     * GET /api/portfolio
     */
    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getPortfolio());
    }

    /**
     * [인증] 관리자 로그인
     * 동일 계정의 다른 세션이 있으면 자동으로 강제 만료(중복 로그인 차단)됩니다.
     * POST /api/auth/login
     * Body: { "userId": "admin", "password": "..." }
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String userId = credentials.get("userId");
        String password = credentials.get("password");

        // 실제 비밀번호 검증 로직은 Spring Security 도입 시 확장 예정
        if (!"admin".equals(userId)) {
            return ResponseEntity.status(401).body(Map.of("error", "인증 실패"));
        }

        String token = sessionManager.createSession(userId);
        return ResponseEntity.ok(Map.of("token", token, "message", "로그인 성공"));
    }

    /**
     * [인증] 로그아웃 — Redis 세션 즉시 삭제
     * POST /api/auth/logout
     * Header: Authorization: Bearer {token}
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            sessionManager.invalidateSession(token);
        }
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    /**
     * [공개] 헬스 체크 — Kubernetes liveness probe용
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "portfolio-api"));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
