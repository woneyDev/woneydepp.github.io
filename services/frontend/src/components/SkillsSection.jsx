const LEVEL_WIDTH = { Advanced: '90%', Intermediate: '60%', Beginner: '30%' };

export default function SkillsSection({ data }) {
  return (
    <section className="section skills">
      <h2 className="section-title">기술 스택</h2>
      <div className="skills-grid">
        {data.map((skill) => (
          <div key={skill.name} className="skill-item">
            <div className="skill-header">
              <span className="skill-name">{skill.name}</span>
              <span className="skill-level">{skill.level}</span>
            </div>
            <div className="skill-bar">
              <div className="skill-bar-fill" style={{ width: LEVEL_WIDTH[skill.level] ?? '50%' }} />
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
