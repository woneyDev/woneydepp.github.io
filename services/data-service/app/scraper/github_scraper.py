import httpx
from fastapi import APIRouter, HTTPException
from app.config import settings
from app.cache.redis_client import cache_get, cache_set, cache_delete

router = APIRouter()

CACHE_KEY = "github:activity"
GITHUB_API = "https://api.github.com"


def _headers() -> dict:
    headers = {"Accept": "application/vnd.github+json"}
    if settings.github_token:
        headers["Authorization"] = f"Bearer {settings.github_token}"
    return headers


async def _fetch_activity() -> dict:
    """GitHub API에서 사용자 활동 데이터를 비동기로 수집합니다."""
    username = settings.github_username
    base = GITHUB_API

    async with httpx.AsyncClient(headers=_headers(), timeout=10.0) as client:
        # 동시 호출로 응답 속도 최적화
        profile_resp, repos_resp, events_resp = await _gather(client, [
            f"{base}/users/{username}",
            f"{base}/users/{username}/repos?sort=updated&per_page=10",
            f"{base}/users/{username}/events/public?per_page=30",
        ])

    profile = profile_resp
    repos = [
        {
            "name": r["name"],
            "description": r.get("description", ""),
            "language": r.get("language", ""),
            "stars": r["stargazers_count"],
            "url": r["html_url"],
            "updated_at": r["updated_at"],
        }
        for r in repos_resp
        if not r.get("fork")  # 포크 제외 — 본인 창작물만
    ]

    # 커밋·PR·이슈 이벤트만 추출
    relevant_types = {"PushEvent", "PullRequestEvent", "IssuesEvent", "CreateEvent"}
    events = [
        {
            "type": e["type"],
            "repo": e["repo"]["name"],
            "created_at": e["created_at"],
        }
        for e in events_resp
        if e["type"] in relevant_types
    ]

    return {
        "username": profile["login"],
        "name": profile.get("name", ""),
        "public_repos": profile["public_repos"],
        "followers": profile["followers"],
        "repos": repos,
        "recent_events": events,
    }


async def _gather(client: httpx.AsyncClient, urls: list[str]) -> list:
    """여러 URL을 순차 호출합니다. (httpx AsyncClient 기반)"""
    results = []
    for url in urls:
        resp = await client.get(url)
        if resp.status_code != 200:
            raise HTTPException(
                status_code=502,
                detail=f"GitHub API 오류: {resp.status_code} — {url}",
            )
        results.append(resp.json())
    return results


@router.get("/activity", summary="GitHub 활동 데이터 조회 (Redis 캐시)")
async def get_activity():
    """
    Redis에 캐시된 데이터가 있으면 즉시 반환합니다.
    없으면 GitHub API를 호출하고 결과를 캐시에 저장합니다.
    """
    cached = cache_get(CACHE_KEY)
    if cached:
        return {"source": "cache", "data": cached}

    data = await _fetch_activity()
    cache_set(CACHE_KEY, data)
    return {"source": "github_api", "data": data}


@router.post("/refresh", summary="GitHub 데이터 강제 갱신")
async def refresh_activity():
    """캐시를 삭제하고 GitHub API에서 최신 데이터를 새로 수집합니다."""
    cache_delete(CACHE_KEY)
    data = await _fetch_activity()
    cache_set(CACHE_KEY, data)
    return {"message": "GitHub 데이터 갱신 완료", "data": data}
