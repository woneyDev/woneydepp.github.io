# 포트폴리오 시스템 구성 매뉴얼

> 이 문서는 현재 구성된 시스템이 **어떤 기술로, 어떤 방식으로 동작하는지** 설명합니다.
> 비용 없이 내 컴퓨터에서 실행되며, 필요 시 AWS로 전환할 수 있도록 설계되어 있습니다.

---

## 전체 흐름 한 눈에 보기

```
[사용자 브라우저]
      │
      ▼
  ① Nginx          ← 문지기. 요청을 받아 적절한 곳으로 안내
      │
      ▼
  React 화면       ← 사용자가 눈으로 보는 포트폴리오 페이지
      │
      ▼ (실시간 데이터 요청)
  Java API         ← 데이터 처리 담당 (2단계에서 구축 예정)
      │
      ├──▶ ④ PostgreSQL  ← 포트폴리오 데이터 저장소
      └──▶ Redis         ← 중복 로그인 차단 + 빠른 데이터 캐시
      │
      ▼
  Python 서비스    ← GitHub 수집 · PDF 생성 · AI 최적화 (3단계 예정)
```

---

## 1. Nginx — 문지기 역할

### 어떤 파일이 담당하나
- `services/frontend/nginx.conf`
- `services/frontend/Dockerfile` (2단계: nginx:alpine 이미지)

### 무슨 일을 하나
Nginx는 사용자의 브라우저 요청을 가장 먼저 받아 처리하는 **웹 서버**입니다.

| 역할 | 설명 |
|---|---|
| 정적 파일 서빙 | HTML · CSS · JS 파일을 빠르게 전달 |
| React 라우팅 처리 | 어떤 페이지 주소로 들어와도 `index.html`로 연결 (SPA 필수 설정) |
| 포트 80 수신 | 표준 웹 포트(80)에서 대기 |

### 핵심 설정 (`nginx.conf`)
```nginx
try_files $uri $uri/ /index.html;
```
이 한 줄이 핵심입니다. "기술 스택", "프로젝트" 등 어떤 메뉴를 눌러도
Nginx가 `index.html`을 돌려줘서 React가 화면 전환을 담당하도록 합니다.

---

## 2. Docker — 포장 및 실행 환경

### 어떤 파일이 담당하나
- `services/frontend/Dockerfile`
- `services/frontend/.dockerignore`
- `docker-compose.yml` (프로젝트 루트)

### 무슨 일을 하나
Docker는 각 서비스를 **독립된 상자(컨테이너)에 넣어 어디서든 동일하게 실행**되도록 보장합니다.
"내 컴퓨터에서는 됐는데 서버에서는 안 된다"는 문제를 근본적으로 차단합니다.

### Dockerfile — 2단계 최적화 빌드

```
1단계 (빌드):  node:24-alpine
               └─ npm ci → npm run build → dist/ 생성

2단계 (실행):  nginx:alpine  ← 초경량 이미지
               └─ dist/ 파일만 주입 → 최종 이미지 크기 최소화
```

| 설정 | 이유 |
|---|---|
| `node:24-alpine` | 프로젝트와 동일한 Node 버전 사용 (호환성) |
| `npm ci` | 매 빌드마다 동일한 버전 보장 (재현성) |
| `.dockerignore` | `node_modules/` · `.env` 등 불필요·민감 파일 제외 (보안·용량) |

### docker-compose.yml — 전체 스택 한 번에 실행

명령어 하나(`docker compose up`)로 아래 5개 컨테이너가 동시에 실행됩니다.

| 컨테이너 | 역할 | 로컬 포트 |
|---|---|---|
| `frontend` | React + Nginx 화면 | 8080 |
| `portfolio-api` | Java Spring Boot API | 8081 |
| `data-service` | Python 자동화 서비스 | 8082 |
| `postgres` | PostgreSQL 데이터베이스 | 5432 |
| `redis` | Redis 캐시 · 세션 관리 | 6379 |

---

## 3. Kubernetes — 서비스 자동 관리 오케스트레이터

### 어떤 파일이 담당하나
```
services/frontend/k8s/
├── base/
│   └── deployment.yaml    ← 로컬·AWS 공통 배포 설정
├── local/
│   └── service.yml        ← 로컬 전용 (NodePort, 비용 0원)
└── aws/
    ├── service.yml        ← AWS 전용 (LoadBalancer)
    └── ingress.yaml       ← AWS ALB 트래픽 분배기
```

### 무슨 일을 하나
Kubernetes는 컨테이너를 **자동으로 관리·복구·분산**하는 시스템입니다.

| 기능 | 설명 |
|---|---|
| 고가용성 | `replicas: 2` — 항상 2개의 컨테이너가 동시 대기 |
| 자동 복구 | 컨테이너 하나가 죽으면 즉시 새 것으로 교체 (Self-healing) |
| 자원 제한 | CPU · 메모리 상한선 설정으로 과부하 방지 |

### 환경별 분기 구조

```
로컬 실행:
  kubectl apply -f k8s/base/deployment.yaml
  kubectl apply -f k8s/local/service.yml
  → NodePort 30080으로 접속 (http://localhost:30080)

AWS 전환 시:
  kubectl apply -f k8s/base/deployment.yaml
  kubectl apply -f k8s/aws/service.yml
  kubectl apply -f k8s/aws/ingress.yaml
  → ALB가 자동 생성되어 외부 주소로 서비스
```

`base/`는 건드리지 않고 `local/` 또는 `aws/` 폴더만 교체하면
동일한 배포 명세가 두 환경에서 모두 작동합니다.

---

## 4. PostgreSQL — 데이터 저장소

### 어떤 파일이 담당하나
- `docker-compose.yml` (postgres 서비스 항목)
- `.env.example` (접속 정보 템플릿)

### 무슨 일을 하나
포트폴리오의 모든 **데이터(프로젝트 목록, 경력, 스킬 등)를 영구적으로 저장**합니다.
Java API 서버(Spring Boot + JPA)가 유일한 접근 창구입니다.

### 로컬 ↔ AWS 전환 방식

| 환경 | 접속 방법 | 변경 파일 |
|---|---|---|
| 로컬 | Docker 컨테이너 `postgres:5432` | `.env`의 `SPRING_DATASOURCE_URL` |
| AWS | RDS 엔드포인트 `{rds-host}:5432` | `.env`의 동일 변수만 교체 |

코드를 한 줄도 바꾸지 않고 `.env` 파일의 주소 하나만 바꾸면 AWS RDS로 전환됩니다.

### 보안 처리 방식
데이터베이스 비밀번호는 **코드에 절대 포함되지 않습니다.**
`.env` 파일(Git에 올라가지 않음)에서 런타임에 주입합니다.
`.env.example`은 어떤 변수가 필요한지 보여주는 안내서 역할만 합니다.

---

## 5. MSA (마이크로서비스 아키텍처) — 서비스 독립 분리

### 어떤 파일이 담당하나
```
services/
├── frontend/          ← 서비스 1: 화면 (React + Nginx)
├── portfolio-api/     ← 서비스 2: Java 메인 API (구축 예정)
├── data-service/      ← 서비스 3: Python 자동화 (구축 예정)
└── shared-infra/      ← 공유 인프라 설정
```

### MSA가 아닌 구조 vs MSA 구조 비교

| 구분 | 일반 구조 | 이 프로젝트 (MSA) |
|---|---|---|
| 서비스 수 | 1개 (모든 기능이 한 곳) | 3개 (역할별 완전 분리) |
| 배포 | 전체를 다시 배포 | 바뀐 서비스만 배포 |
| 장애 | 하나가 죽으면 전체 중단 | 해당 서비스만 영향 |
| 확장 | 전체를 키워야 함 | 트래픽 많은 서비스만 확장 |

### 서비스 간 통신 규칙

```
Frontend  ──fetch──▶  Java API  (HTTP REST)
Java API  ────────▶  Python    (HTTP REST)
Java API  ────────▶  Redis     (캐시 조회)
Java API  ────────▶  PostgreSQL (데이터 읽기·쓰기)
Python    ────────▶  Redis     (결과 캐싱)
```

각 서비스는 **API(약속된 통신 규격)로만** 대화합니다.
내부 코드가 어떻게 생겼는지 서로 알 필요가 없습니다.

---

## 로컬 실행 방법 (전체 스택)

```bash
# 1. .env.example을 복사해서 실제 값을 채운다
cp .env.example .env

# 2. 전체 시스템을 한 번에 실행
docker compose up

# 3. 브라우저로 접속
#    화면:    http://localhost:8080
#    Java API: http://localhost:8081
#    Python:   http://localhost:8082
```

## AWS 전환 시 변경점 요약

| 항목 | 로컬 | AWS |
|---|---|---|
| 화면 호스팅 | Docker + K8s local | S3 + CloudFront 또는 EKS |
| Java API | Docker 컨테이너 | ECS 또는 EKS |
| PostgreSQL | Docker postgres 컨테이너 | RDS |
| Redis | Docker redis 컨테이너 | ElastiCache |
| K8s 설정 | `k8s/local/` | `k8s/aws/` |
| 환경변수 | `.env` 파일 | AWS Secrets Manager 또는 Parameter Store |
