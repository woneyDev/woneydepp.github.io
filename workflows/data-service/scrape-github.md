# Workflow: data-service/scrape-github

## 목적
GitHub API를 통해 개발자 활동 데이터(커밋 수, 기여 현황 등)를 자동 수집하고 Redis에 캐싱한다.

## 입력값
- GitHub Personal Access Token (환경 변수로 설정)
- 수집 대상 GitHub 사용자명

## 실행 순서

### Step 1. Python 환경 세팅
```
cd services/data-service
pip install -r requirements.txt
```

### Step 2. 환경 변수 설정
`.env` 파일에 `GITHUB_TOKEN`, `REDIS_URL` 설정.

### Step 3. 스크래퍼 실행
```
python tools/github_scraper.py
```

### Step 4. 결과 확인
Redis에 캐싱된 데이터 확인. Java API `/api/github/stats` 엔드포인트 응답 확인.

## 결과물
- GitHub 활동 데이터가 Redis에 저장됨
- Frontend 화면의 GitHub 통계 섹션에 실시간 반영
