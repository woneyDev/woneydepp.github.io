export default function ProjectsSection({ data }) {
  return (
    <section className="section projects">
      <h2 className="section-title">프로젝트</h2>
      <div className="projects-grid">
        {data.map((project) => (
          <div key={project.title} className="project-card">
            <div className="project-period">{project.period}</div>
            <h3 className="project-title">{project.title}</h3>
            <p className="project-desc">{project.description}</p>
            <div className="tech-stack">
              {project.techStack.map((tech) => (
                <span key={tech} className="tech-badge">{tech}</span>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
