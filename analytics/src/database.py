from __future__ import annotations

import pandas as pd
from sqlalchemy import Engine, create_engine, text


READ_EVENTS = text(
    """
    SELECT driver_id, speed_kmh, hard_braking, rapid_acceleration, odometer_km
    FROM telemetry_events
    WHERE (:driver_id IS NULL OR driver_id = :driver_id)
    ORDER BY driver_id, event_timestamp, id
    """
)

UPSERT_ANALYTICS = text(
    """
    INSERT INTO driver_analytics (
        driver_id, average_speed_kmh, max_speed_kmh, speeding_event_count,
        hard_braking_count, rapid_acceleration_count, event_count,
        estimated_distance_km, risk_score, calculated_at
    ) VALUES (
        :driver_id, :average_speed_kmh, :max_speed_kmh, :speeding_event_count,
        :hard_braking_count, :rapid_acceleration_count, :event_count,
        :estimated_distance_km, :risk_score, :calculated_at
    )
    ON DUPLICATE KEY UPDATE
        average_speed_kmh = VALUES(average_speed_kmh),
        max_speed_kmh = VALUES(max_speed_kmh),
        speeding_event_count = VALUES(speeding_event_count),
        hard_braking_count = VALUES(hard_braking_count),
        rapid_acceleration_count = VALUES(rapid_acceleration_count),
        event_count = VALUES(event_count),
        estimated_distance_km = VALUES(estimated_distance_km),
        risk_score = VALUES(risk_score),
        calculated_at = VALUES(calculated_at)
    """
)


def make_engine(database_url: str) -> Engine:
    return create_engine(database_url, pool_pre_ping=True)


def read_events(engine: Engine, driver_id: int | None = None) -> pd.DataFrame:
    with engine.connect() as connection:
        return pd.read_sql(READ_EVENTS, connection, params={"driver_id": driver_id})


def write_analytics(engine: Engine, analytics: pd.DataFrame) -> int:
    if analytics.empty:
        return 0
    records = analytics.to_dict(orient="records")
    with engine.begin() as connection:
        connection.execute(UPSERT_ANALYTICS, records)
    return len(records)
