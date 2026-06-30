export default function HeroSection({ data }) {
  return (
    <section className="hero">
      <div className="hero-content">
        <h1>{data.title}</h1>
        <p className="hero-subtitle">{data.subtitle}</p>
        <div className="hero-links">
          <a href={`mailto:${data.email}`} className="btn btn-primary">이메일 문의</a>
          <a href={data.github} target="_blank" rel="noreferrer" className="btn btn-outline">GitHub</a>
        </div>
      </div>
    </section>
  );
}
