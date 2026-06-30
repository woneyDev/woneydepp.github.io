from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # GitHub
    github_token: str = ""
    github_username: str = "woneyDev"

    # Redis
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_ttl_seconds: int = 3600  # GitHub 데이터 캐시 유효 시간

    # AI (Anthropic)
    anthropic_api_key: str = ""

    # 서버
    server_port: int = 8080

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False


settings = Settings()
