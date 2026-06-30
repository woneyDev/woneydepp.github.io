# Workflow: frontend/deploy-frontend

## 목적
완성된 React 화면을 GitHub Pages에 배포한다.

## 사전 조건
- GitHub 저장소가 생성되어 있어야 함
- git remote가 연결되어 있어야 함

## 실행 순서

### Step 1. 빌드 및 배포
```
cd services/frontend
npm run deploy
```
(내부적으로 `npm run build` → `gh-pages -d dist` 순서로 자동 실행됨)

### Step 2. 배포 확인
2-3분 후 https://woneydepp.github.io 접속하여 확인.

## 문제 발생 시
- `fatal: not a git repository` → 프로젝트 루트에서 `git init` 후 remote 연결 필요
- 화면이 안 보일 때 → GitHub 저장소 Settings > Pages > Source가 `gh-pages` 브랜치로 설정되어 있는지 확인
