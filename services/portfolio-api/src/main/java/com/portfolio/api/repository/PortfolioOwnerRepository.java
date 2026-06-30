package com.portfolio.api.repository;

import com.portfolio.api.entity.PortfolioOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PortfolioOwnerRepository extends JpaRepository<PortfolioOwner, Long> {

    /**
     * 포트폴리오 소유자를 스킬·프로젝트·경력과 함께 한 번에 조회합니다.
     * N+1 문제를 방지하기 위해 FETCH JOIN을 사용합니다.
     */
    @Query("""
            SELECT DISTINCT o FROM PortfolioOwner o
            LEFT JOIN FETCH o.skills
            LEFT JOIN FETCH o.projects
            LEFT JOIN FETCH o.careers
            """)
    Optional<PortfolioOwner> findFirstWithAll();
}
