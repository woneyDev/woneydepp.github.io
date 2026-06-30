# Workflow: frontend/orchestrate-k8s

## 목적
로컬 Kubernetes 환경에서 포트폴리오 서비스를 배포하고 고가용성(2개 복제) 및 트래픽 분산 구조를 검증한다.

## 사전 조건
- Docker Desktop이 설치되어 있고 Kubernetes 기능이 활성화되어 있어야 함
  (Docker Desktop → Settings → Kubernetes → Enable Kubernetes 체크)
- `sangmu/portfolio:latest` 이미지가 로컬에 빌드되어 있어야 함
  (containerize-nginx Workflow 먼저 실행)

## 실행 순서

### Step 1. Kubernetes 클러스터 상태 확인
```
kubectl cluster-info
kubectl get nodes
```

### Step 2. 배포 적용
```
cd services/frontend
kubectl apply -f k8s/deployment.yaml
```

### Step 3. 배포 상태 확인
```
kubectl get pods          # 2개의 Pod가 Running 상태인지 확인
kubectl get services      # portfolio-service 확인
```

### Step 4. 브라우저 접속 확인
```
kubectl port-forward service/portfolio-service 8080:80
```
http://localhost:8080 접속하여 포트폴리오 화면 확인.

### Step 5. 정리 (테스트 완료 후)
```
kubectl delete -f k8s/deployment.yaml
```

## 결과물
- 로컬 K8s 클러스터에 2개의 컨테이너(Pod)가 동시 운영됨
- Self-healing 검증: Pod 하나를 강제 종료해도 자동으로 재생성됨

## 문제 발생 시
- `kubectl: command not found` → Docker Desktop의 Kubernetes 활성화 필요
- Pod이 Pending 상태 → `kubectl describe pod {pod명}` 으로 원인 확인
