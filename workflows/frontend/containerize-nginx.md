# Workflow: frontend/containerize-nginx

## 목적
React 빌드 결과물과 Nginx를 하나의 Docker 이미지로 묶어 컨테이너를 생성한다.

## 사전 조건
- Docker Desktop이 설치되어 실행 중이어야 함

## 실행 순서

### Step 1. Docker 이미지 빌드
```
cd services/frontend
docker build -t sangmu/portfolio:latest .
```
(내부적으로 Node 빌드 → Nginx 이미지 주입의 2단계로 자동 진행됨)

### Step 2. 로컬에서 컨테이너 실행 및 확인
```
docker run -p 8080:80 sangmu/portfolio:latest
```
브라우저에서 http://localhost:8080 접속하여 포트폴리오 화면 확인.

### Step 3. 확인 후 컨테이너 종료
```
docker ps                          # 실행 중인 컨테이너 ID 확인
docker stop {컨테이너ID}
```

## 결과물
- `sangmu/portfolio:latest` Docker 이미지 (로컬 저장)
- Nginx 웹 서버 위에서 React 포트폴리오가 정상 서빙됨을 검증

## 문제 발생 시
- `docker: command not found` → Docker Desktop 설치 및 실행 확인
- 빌드 실패 → `npm run build` 가 로컬에서 먼저 성공하는지 확인
