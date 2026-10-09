from __future__ import annotations

import pytest

from src.config import Settings


def test_builds_database_url_from_environment(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.delenv("DATABASE_URL", raising=False)
    monkeypatch.setenv("MYSQL_USER", "fleet user")
    monkeypatch.setenv("MYSQL_PASSWORD", "p@ss")
    monkeypatch.setenv("MYSQL_HOST", "db")
    settings = Settings.from_environment()
    assert settings.database_url.startswith("mysql+pymysql://fleet+user:p%40ss@db:")


def test_rejects_non_positive_threshold(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setenv("SPEEDING_THRESHOLD_KMH", "0")
    with pytest.raises(ValueError, match="greater than zero"):
        Settings.from_environment()
