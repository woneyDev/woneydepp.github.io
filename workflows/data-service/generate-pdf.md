# Workflow: data-service/generate-pdf

## 목적
portfolio.json 데이터를 기반으로 PDF 이력서를 자동 생성한다.

## 실행 순서

### Step 1. PDF 생성 실행
```
cd services/data-service
python tools/pdf_generator.py
```

### Step 2. 결과물 확인
`output/resume.pdf` 파일이 생성되었는지 확인.

## 결과물
- `services/data-service/output/resume.pdf` — 완성된 PDF 이력서
