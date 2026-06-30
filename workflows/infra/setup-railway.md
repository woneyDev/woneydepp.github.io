# Workflow: infra/setup-railway

## 목적
Railway 플랫폼에 PostgreSQL 데이터베이스와 Redis를 세팅하고, Java·Python 서비스가 사용할 수 있도록 연결한다.

## 실행 순서

### Step 1. Railway 프로젝트 생성
Railway (railway.app) 가입 후 새 프로젝트 생성.

### Step 2. PostgreSQL 추가
프로젝트 내 "New Service" → "Database" → "PostgreSQL" 선택.
생성 후 `DATABASE_URL` 환경 변수 복사.

### Step 3. Redis 추가
프로젝트 내 "New Service" → "Database" → "Redis" 선택.
생성 후 `REDIS_URL` 환경 변수 복사.

### Step 4. 서비스 환경 변수 설정
- `services/portfolio-api` Railway 서비스에 `DATABASE_URL`, `REDIS_URL` 추가
- `services/data-service` Railway 서비스에 `REDIS_URL`, `GITHUB_TOKEN` 추가

## 결과물
- PostgreSQL DB 및 Redis가 Railway 내에서 실행 중
- Java·Python 서비스가 DB·Redis와 정상 연결됨
