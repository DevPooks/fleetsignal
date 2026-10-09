from __future__ import annotations

import pandas as pd
import pytest

from src.calculations import OUTPUT_COLUMNS, calculate_driver_analytics


def test_calculates_deterministic_metrics_for_each_driver() -> None:
    events = pd.DataFrame(
        [
            {"driver_id": 1, "speed_kmh": 80, "hard_braking": False, "rapid_acceleration": True, "odometer_km": 100.0},
            {"driver_id": 1, "speed_kmh": 120, "hard_braking": True, "rapid_acceleration": False, "odometer_km": 112.5},
            {"driver_id": 1, "speed_kmh": 101, "hard_braking": False, "rapid_acceleration": False, "odometer_km": 115.0},
            {"driver_id": 2, "speed_kmh": 50, "hard_braking": False, "rapid_acceleration": False, "odometer_km": 8.0},
        ]
    )

    result = calculate_driver_analytics(events, speeding_threshold_kmh=100)
    driver = result[result["driver_id"] == 1].iloc[0]

    assert driver["average_speed_kmh"] == pytest.approx(100.33)
    assert driver["max_speed_kmh"] == 120
    assert driver["speeding_event_count"] == 2
    assert driver["hard_braking_count"] == 1
    assert driver["rapid_acceleration_count"] == 1
    assert driver["event_count"] == 3
    assert driver["estimated_distance_km"] == 15
    assert driver["risk_score"] == 9


def test_empty_input_returns_stable_schema() -> None:
    result = calculate_driver_analytics(pd.DataFrame(), 100)
    assert result.empty
    assert list(result.columns) == OUTPUT_COLUMNS


def test_drops_malformed_and_out_of_range_measurements() -> None:
    events = pd.DataFrame(
        [
            {"driver_id": 1, "speed_kmh": "bad", "hard_braking": False, "rapid_acceleration": False, "odometer_km": 10},
            {"driver_id": 1, "speed_kmh": -2, "hard_braking": False, "rapid_acceleration": False, "odometer_km": 11},
            {"driver_id": 1, "speed_kmh": 40, "hard_braking": None, "rapid_acceleration": None, "odometer_km": 12},
        ]
    )

    result = calculate_driver_analytics(events, 100)

    assert result.iloc[0]["event_count"] == 1
    assert result.iloc[0]["hard_braking_count"] == 0


def test_requires_expected_columns() -> None:
    with pytest.raises(ValueError, match="Missing telemetry columns"):
        calculate_driver_analytics(pd.DataFrame([{"driver_id": 1}]), 100)


def test_caps_risk_score_at_one_hundred() -> None:
    events = pd.DataFrame(
        [
            {"driver_id": 1, "speed_kmh": 150, "hard_braking": True, "rapid_acceleration": True, "odometer_km": index}
            for index in range(20)
        ]
    )
    result = calculate_driver_analytics(events, 100)
    assert result.iloc[0]["risk_score"] == 100
