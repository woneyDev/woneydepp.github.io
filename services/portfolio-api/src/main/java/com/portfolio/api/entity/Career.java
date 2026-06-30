package com.portfolio.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "career")
@Getter @Setter @NoArgsConstructor
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String period;

    /**
     * 주요 성과 목록 — 별도 테이블(career_achievement)로 저장됩니다.
     */
    @ElementCollection
    @CollectionTable(name = "career_achievement", joinColumns = @JoinColumn(name = "career_id"))
    @Column(name = "achievement", length = 500)
    private List<String> achievements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private PortfolioOwner owner;
}
