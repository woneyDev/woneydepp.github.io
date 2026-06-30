import anthropic
from fastapi import APIRouter, HTTPException
from app.config import settings

router = APIRouter()

SYSTEM_PROMPT = """당신은 개발자 포트폴리오 전문 카피라이터입니다.
주어진 포트폴리오 데이터를 분석하여 다음 기준으로 개선 제안을 반환합니다:
1. 채용 담당자 시선에서 임팩트 있는 문구로 수정
2. 수치·성과 중심 표현으로 전환 (예: "개발했다" → "처리 시간 40% 단축")
3. 기술 스택 표현을 현대적 트렌드에 맞게 조정
JSON 형식으로 반환하세요. 예시:
{
  "hero_subtitle": "개선된 한 줄 소개",
  "project_descriptions": { "원본 제목": "개선된 설명" },
  "career_achievements": { "회사명": ["개선된 성과1", "개선된 성과2"] }
}"""


@router.post("/optimize", summary="AI 포트폴리오 최적화 제안")
async def optimize_portfolio(data: dict):
    """
    포트폴리오 데이터를 Claude AI에 전달하여 문구 개선 제안을 받습니다.
    ANTHROPIC_API_KEY 환경변수가 없으면 오류를 반환합니다.
    """
    if not settings.anthropic_api_key:
        raise HTTPException(
            status_code=503,
            detail="ANTHROPIC_API_KEY가 설정되지 않았습니다. .env 파일을 확인하세요.",
        )

    client = anthropic.Anthropic(api_key=settings.anthropic_api_key)

    user_message = f"""다음 포트폴리오 데이터를 분석하고 개선안을 제안해주세요:

{data}

각 항목에 대해 구체적인 수정 제안을 JSON으로 반환해주세요."""

    try:
        message = client.messages.create(
            model="claude-sonnet-4-6",
            max_tokens=2048,
            system=SYSTEM_PROMPT,
            messages=[{"role": "user", "content": user_message}],
        )
        raw_text = message.content[0].text

        # JSON 파싱 시도 — 실패 시 원문 그대로 반환
        import json
        try:
            suggestions = json.loads(raw_text)
        except json.JSONDecodeError:
            suggestions = {"raw_suggestion": raw_text}

        return {
            "model": "claude-sonnet-4-6",
            "input_tokens": message.usage.input_tokens,
            "output_tokens": message.usage.output_tokens,
            "suggestions": suggestions,
        }

    except anthropic.APIError as e:
        raise HTTPException(status_code=502, detail=f"Anthropic API 오류: {e}")
