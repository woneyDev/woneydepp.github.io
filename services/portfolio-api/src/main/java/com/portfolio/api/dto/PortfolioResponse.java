package com.portfolio.api.dto;

import com.portfolio.api.entity.Career;
import com.portfolio.api.entity.PortfolioOwner;
import com.portfolio.api.entity.Project;
import com.portfolio.api.entity.Skill;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioResponse {

    private final HeroDto hero;
    private final List<SkillDto> skills;
    private final List<ProjectDto> projects;
    private final List<CareerDto> career;

    public PortfolioResponse(PortfolioOwner owner) {
        this.hero = new HeroDto(owner);
        this.skills = owner.getSkills().stream().map(SkillDto::new).toList();
        this.projects = owner.getProjects().stream().map(ProjectDto::new).toList();
        this.career = owner.getCareers().stream().map(CareerDto::new).toList();
    }

    public record HeroDto(String title, String subtitle, String email, String githubUrl) {
        public HeroDto(PortfolioOwner o) {
            this(o.getTitle(), o.getSubtitle(), o.getEmail(), o.getGithubUrl());
        }
    }

    public record SkillDto(String name, String level) {
        public SkillDto(Skill s) {
            this(s.getName(), s.getLevel());
        }
    }

    public record ProjectDto(String title, String description, String period, List<String> techStack) {
        public ProjectDto(Project p) {
            this(p.getTitle(), p.getDescription(), p.getPeriod(), p.getTechStack());
        }
    }

    public record CareerDto(String company, String role, String period, List<String> achievements) {
        public CareerDto(Career c) {
            this(c.getCompany(), c.getRole(), c.getPeriod(), c.getAchievements());
        }
    }
}
