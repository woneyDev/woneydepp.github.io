# Workflow: portfolio-api/manage-portfolio-data

## 목적
Java API 서버와 연결된 데이터베이스에서 포트폴리오 데이터를 추가·수정·삭제한다.

## 실행 순서

### Step 1. 로컬 서버 실행
```
cd services/portfolio-api
./mvnw spring-boot:run
```

### Step 2. API 요청으로 데이터 조작
- 조회: GET  http://localhost:8080/api/portfolio
- 추가: POST http://localhost:8080/api/portfolio
- 수정: PUT  http://localhost:8080/api/portfolio/{id}
- 삭제: DELETE http://localhost:8080/api/portfolio/{id}

### Step 3. 변경사항 Railway에 배포
```
git push railway main
```

## 결과물
- 데이터베이스에 포트폴리오 데이터 반영
- Frontend의 비동기 fetch 호출 시 최신 데이터 응답
