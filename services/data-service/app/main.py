from fastapi import FastAPI
from fastapi.responses import JSONResponse

from app.scraper.github_scraper import router as github_router
from app.generator.pdf_generator import router as pdf_router
from app.optimizer.ai_optimizer import router as ai_router

app = FastAPI(
    title="Portfolio Data Service",
    description="GitHub 스크래핑 · PDF 생성 · AI 최적화 담당 Python 서비스",
    version="1.0.0",
)

app.include_router(github_router, prefix="/api/github", tags=["GitHub"])
app.include_router(pdf_router,    prefix="/api/pdf",    tags=["PDF"])
app.include_router(ai_router,     prefix="/api/ai",     tags=["AI"])


@app.get("/health", tags=["Infra"])
async def health():
    """Kubernetes liveness/readiness probe 전용 엔드포인트."""
    return JSONResponse({"status": "UP", "service": "data-service"})
