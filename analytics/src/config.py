from __future__ import annotations

import os
from dataclasses import dataclass
from urllib.parse import quote_plus


@dataclass(frozen=True)
class Settings:
    database_url: str
    speeding_threshold_kmh: float = 100.0

    @classmethod
    def from_environment(cls) -> "Settings":
        explicit_url = os.getenv("DATABASE_URL")
        if explicit_url:
            database_url = explicit_url
        else:
            user = quote_plus(os.getenv("MYSQL_USER", "fleetsignal"))
            password = quote_plus(os.getenv("MYSQL_PASSWORD", "change-me"))
            host = os.getenv("MYSQL_HOST", "localhost")
            port = os.getenv("MYSQL_PORT", "3306")
            database = os.getenv("MYSQL_DATABASE", "fleetsignal")
            database_url = f"mysql+pymysql://{user}:{password}@{host}:{port}/{database}"

        threshold = float(os.getenv("SPEEDING_THRESHOLD_KMH", "100"))
        if threshold <= 0:
            raise ValueError("SPEEDING_THRESHOLD_KMH must be greater than zero")
        return cls(database_url=database_url, speeding_threshold_kmh=threshold)
