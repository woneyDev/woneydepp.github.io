import HeroSection from './components/HeroSection';
import SkillsSection from './components/SkillsSection';
import ProjectsSection from './components/ProjectsSection';
import CareerSection from './components/CareerSection';
import portfolioData from './data/portfolio.json';
import './App.css';

function App() {
  return (
    <div className="portfolio">
      <nav className="navbar">
        <span className="nav-brand">Portfolio</span>
        <div className="nav-links">
          <a href="#skills">기술 스택</a>
          <a href="#projects">프로젝트</a>
          <a href="#career">경력</a>
        </div>
      </nav>

      <main>
        <HeroSection data={portfolioData.hero} />
        <div id="skills"><SkillsSection data={portfolioData.skills} /></div>
        <div id="projects"><ProjectsSection data={portfolioData.projects} /></div>
        <div id="career"><CareerSection data={portfolioData.career} /></div>
      </main>

      <footer className="footer">
        <p>© 2026 · Built with React · Hosted on GitHub Pages</p>
      </footer>
    </div>
  );
}

export default App;
