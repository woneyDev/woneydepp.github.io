package com.portfolio.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 포트폴리오 소유자 정보 — 시스템 내 단 한 명의 레코드만 존재합니다.
 * Skill / Project / Career 와 1:N 관계를 가집니다.
 */
@Entity
@Table(name = "portfolio_owner")
@Getter @Setter @NoArgsConstructor
public class PortfolioOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String subtitle;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String githubUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();
}
