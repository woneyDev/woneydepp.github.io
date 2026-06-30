package com.portfolio.api.service;

import com.portfolio.api.dto.PortfolioResponse;
import com.portfolio.api.entity.PortfolioOwner;
import com.portfolio.api.repository.PortfolioOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트랜잭션 설계 원칙
 * ─────────────────────────────────────────────────────────────────
 * 1. 클래스 레벨 @Transactional(readOnly = true)
 *    → 조회 전용 메서드가 대부분이므로 기본값을 읽기 전용으로 설정합니다.
 *    → readOnly=true 는 JPA 더티 체킹(Dirty Checking)을 비활성화하여
 *      불필요한 스냅샷 메모리 및 flush 연산을 제거합니다.
 *
 * 2. 쓰기 메서드에 @Transactional (readOnly 없음)
 *    → 명시적으로 REQUIRED + 쓰기 허용 트랜잭션을 열어 예외 발생 시 자동 롤백합니다.
 *    → RuntimeException 발생 → 자동 롤백 / Checked Exception → 기본적으로 롤백 안 함
 *      (CLAUDE.md 규정: 모든 비즈니스 예외는 RuntimeException 계열로 통일)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioOwnerRepository ownerRepository;

    /**
     * 포트폴리오 전체 데이터 조회 (읽기 전용 트랜잭션 상속)
     * FETCH JOIN으로 스킬·프로젝트·경력을 단일 쿼리에 로드합니다.
     */
    public PortfolioResponse getPortfolio() {
        PortfolioOwner owner = ownerRepository.findFirstWithAll()
                .orElseThrow(() -> new IllegalStateException("포트폴리오 데이터가 존재하지 않습니다."));
        return new PortfolioResponse(owner);
    }

    /**
     * 포트폴리오 소유자 정보 저장 (쓰기 트랜잭션 — 실패 시 전체 롤백)
     */
    @Transactional
    public PortfolioOwner save(PortfolioOwner owner) {
        return ownerRepository.save(owner);
    }
}
