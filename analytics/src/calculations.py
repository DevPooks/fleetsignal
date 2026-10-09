from __future__ import annotations

import pandas as pd


OUTPUT_COLUMNS = [
    "driver_id",
    "average_speed_kmh",
    "max_speed_kmh",
    "speeding_event_count",
    "hard_braking_count",
    "rapid_acceleration_count",
    "event_count",
    "estimated_distance_km",
    "risk_score",
    "calculated_at",
]


def calculate_driver_analytics(
    events: pd.DataFrame, speeding_threshold_kmh: float
) -> pd.DataFrame:
    """Aggregate validated telemetry into one deterministic row per driver."""
    if events.empty:
        return pd.DataFrame(columns=OUTPUT_COLUMNS)

    required = {"driver_id", "speed_kmh", "hard_braking", "rapid_acceleration", "odometer_km"}
    missing = required.difference(events.columns)
    if missing:
        raise ValueError(f"Missing telemetry columns: {', '.join(sorted(missing))}")

    cleaned = events.copy()
    for column in ("driver_id", "speed_kmh", "odometer_km"):
        cleaned[column] = pd.to_numeric(cleaned[column], errors="coerce")
    cleaned = cleaned.dropna(subset=["driver_id", "speed_kmh", "odometer_km"])
    cleaned = cleaned[
        cleaned["speed_kmh"].between(0, 250) & (cleaned["odometer_km"] >= 0)
    ]
    if cleaned.empty:
        return pd.DataFrame(columns=OUTPUT_COLUMNS)

    cleaned["driver_id"] = cleaned["driver_id"].astype("int64")
    cleaned["hard_braking"] = cleaned["hard_braking"].map(
        lambda value: bool(value) if pd.notna(value) else False
    )
    cleaned["rapid_acceleration"] = cleaned["rapid_acceleration"].map(
        lambda value: bool(value) if pd.notna(value) else False
    )
    cleaned["is_speeding"] = cleaned["speed_kmh"] > speeding_threshold_kmh

    rows: list[dict[str, object]] = []
    calculated_at = pd.Timestamp.now(tz="UTC")
    for driver_id, group in cleaned.groupby("driver_id", sort=True):
        speeding_count = int(group["is_speeding"].sum())
        braking_count = int(group["hard_braking"].sum())
        acceleration_count = int(group["rapid_acceleration"].sum())
        distance = max(float(group["odometer_km"].max() - group["odometer_km"].min()), 0.0)
        score = min(100.0, speeding_count * 2 + braking_count * 3 + acceleration_count * 2)
        rows.append(
            {
                "driver_id": int(driver_id),
                "average_speed_kmh": round(float(group["speed_kmh"].mean()), 2),
                "max_speed_kmh": round(float(group["speed_kmh"].max()), 2),
                "speeding_event_count": speeding_count,
                "hard_braking_count": braking_count,
                "rapid_acceleration_count": acceleration_count,
                "event_count": len(group),
                "estimated_distance_km": round(distance, 2),
                "risk_score": round(score, 2),
                "calculated_at": calculated_at.to_pydatetime(),
            }
        )
    return pd.DataFrame(rows, columns=OUTPUT_COLUMNS)
