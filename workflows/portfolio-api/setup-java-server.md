# Workflow: portfolio-api/setup-java-server

## 목적
Java 25 기반 Spring Boot API 서버를 최초 세팅하고 Railway에 배포 준비를 완료한다.

## 기술 스택
- Java 25 (LTS)
- Spring Boot 3
- JPA (데이터베이스 연동)
- Redis (중복 로그인 차단 + 서비스 간 캐싱)
- PostgreSQL (메인 데이터베이스)

## 실행 순서

### Step 1. Spring Boot 프로젝트 생성
`services/portfolio-api/` 에 Spring Boot 프로젝트를 초기화한다.
의존성: Spring Web, Spring Data JPA, Spring Data Redis, PostgreSQL Driver

### Step 2. 환경 변수 설정
`.env` 파일에 DB 접속 정보, Redis 접속 정보 설정.

### Step 3. 핵심 Tool 파일 생성
- `ApiController.java` — REST API 엔드포인트
- `PortfolioRepository.java` — JPA DB 조회
- `SessionManager.java` — Redis 중복 로그인 차단

### Step 4. 로컬 실행 확인
```
cd services/portfolio-api
./mvnw spring-boot:run
```

### Step 5. Railway 배포
Railway 프로젝트와 연결 후 배포.

## 결과물
- `https://{railway-app}.railway.app/api/portfolio` API 응답 확인
