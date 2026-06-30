const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function request(path, options = {}) {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) throw new Error(`API 오류: ${res.status}`);
  return res.json();
}

export const api = {
  getPortfolio: () => request('/api/portfolio'),
  getGithubStats: () => request('/api/github/stats'),
  generatePdf: () => request('/api/pdf/generate', { method: 'POST' }),
};
