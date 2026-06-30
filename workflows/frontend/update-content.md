# Workflow: frontend/update-content

## 목적
portfolio.json 내용을 수정하고 화면에 반영한다.

## 입력값
- 수정할 항목 (hero / skills / projects / career)
- 변경할 내용

## 실행 순서

### Step 1. 데이터 파일 수정
`services/frontend/src/data/portfolio.json` 파일에서 해당 항목을 수정한다.

### Step 2. 로컬에서 확인
```
cd services/frontend
npm run dev
```
브라우저에서 http://localhost:5173 접속하여 수정 결과 확인.

### Step 3. 배포 반영
```
npm run deploy
```

## 결과물
- 변경된 내용이 https://woneydepp.github.io 에 반영됨
