import json
import redis
from app.config import settings


def get_redis() -> redis.Redis:
    return redis.Redis(
        host=settings.redis_host,
        port=settings.redis_port,
        decode_responses=True,
    )


def cache_get(key: str) -> dict | None:
    r = get_redis()
    raw = r.get(key)
    if raw is None:
        return None
    return json.loads(raw)


def cache_set(key: str, value: dict, ttl: int = None) -> None:
    r = get_redis()
    r.setex(key, ttl or settings.redis_ttl_seconds, json.dumps(value, ensure_ascii=False))


def cache_delete(key: str) -> None:
    get_redis().delete(key)
