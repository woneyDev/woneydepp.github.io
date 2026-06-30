# Workflow: portfolio-api/deploy-java-api

## 목적
Java Spring Boot 서버를 Railway에 배포한다.

## 사전 조건
- Railway 계정 및 프로젝트 생성 완료
- PostgreSQL, Redis 서비스가 Railway 내에 추가되어 있어야 함 (infra/setup-railway 완료)

## 실행 순서

### Step 1. 빌드 확인
```
cd services/portfolio-api
./mvnw clean package -DskipTests
```

### Step 2. Railway 배포
```
git push railway main
```

### Step 3. 배포 확인
Railway 대시보드 → Logs 탭에서 서버 정상 기동 확인.
`https://{railway-app}.railway.app/api/portfolio` 접속하여 JSON 응답 확인.
