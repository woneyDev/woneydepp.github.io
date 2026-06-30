import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  return {
    // VITE_BASE_PATH 환경변수가 있으면 사용하고, 없으면 '/' 사용
    // GitHub Pages 배포: VITE_BASE_PATH=/woneydepp.github.io/
    // Docker 로컬 실행: 환경변수 없음 → '/' 자동 사용
    base: env.VITE_BASE_PATH || '/',
    plugins: [react()],
  };
});
