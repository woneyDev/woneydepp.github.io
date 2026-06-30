# Workflow: frontend/setup

## 목적
React 기반 포트폴리오 화면 프로젝트를 최초 1회 세팅하고 GitHub Pages 배포 준비를 완료한다.

## 입력값 (필요한 정보)
- GitHub 사용자명 (예: `woneydepp`) → GitHub Pages 주소가 `https://woneydepp.github.io`로 결정됨
- 포트폴리오 소유자 이름, 직군, 한 줄 소개

## 실행 순서

### Step 1. React 프로젝트 초기화
```
cd services/frontend
npm install
```

### Step 2. 로컬 개발 서버 실행 (확인용)
```
npm run dev
```
브라우저에서 http://localhost:5173 접속하여 화면 확인.

### Step 3. GitHub Pages 배포 설정
`vite.config.js`의 `base` 값을 GitHub 사용자명에 맞게 수정한다.
→ tools/frontend/vite.config.js 참고

### Step 4. 첫 배포
```
npm run deploy
```

## 결과물
- `services/frontend/` — 완성된 React 프로젝트
- GitHub Pages URL: `https://{사용자명}.github.io`

## 문제 발생 시
- npm install 실패 → Node.js 24 이상인지 확인 (`node --version`)
- 배포 실패 → GitHub 저장소가 생성되어 있는지 확인
- 화면이 안 나올 때 → `npm run build` 후 `dist/` 폴더 내용 확인
