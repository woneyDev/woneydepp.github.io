export default function CareerSection({ data }) {
  return (
    <section className="section career">
      <h2 className="section-title">경력</h2>
      <div className="timeline">
        {data.map((item) => (
          <div key={item.company} className="timeline-item">
            <div className="timeline-marker" />
            <div className="timeline-body">
              <div className="timeline-header">
                <span className="company">{item.company}</span>
                <span className="period">{item.period}</span>
              </div>
              <div className="role">{item.role}</div>
              <ul className="achievements">
                {item.achievements.map((a) => (
                  <li key={a}>{a}</li>
                ))}
              </ul>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
