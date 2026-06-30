# Workflow: data-service/ai-optimize

## 목적
AI API를 활용하여 현재 포트폴리오 내용을 분석하고 개선 제안을 생성한다.

## 입력값
- AI API Key (환경 변수로 설정)
- 분석 대상: portfolio.json

## 실행 순서

### Step 1. AI 최적화 실행
```
cd services/data-service
python tools/ai_optimizer.py
```

### Step 2. 결과 확인
터미널에 출력되는 개선 제안 내용을 검토.
마음에 드는 제안은 직접 portfolio.json에 반영.

## 결과물
- 포트폴리오 문구 개선 제안 리포트 (터미널 출력 또는 `output/suggestions.txt`)
