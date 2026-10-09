#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import random
import sys
from datetime import UTC, datetime, timedelta
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


def post_json(base_url: str, path: str, payload: dict[str, object]) -> dict[str, object]:
    request = Request(
        f"{base_url.rstrip('/')}{path}",
        data=json.dumps(payload).encode(),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urlopen(request, timeout=10) as response:
        return json.loads(response.read())


def generate(args: argparse.Namespace) -> int:
    randomizer = random.Random(args.seed)
    drivers: list[int] = []
    vehicles: list[int] = []
    stamp = int(datetime.now(UTC).timestamp())

    for index in range(args.drivers):
        driver = post_json(
            args.api_url,
            "/api/v1/drivers",
            {
                "externalReference": f"demo-driver-{stamp}-{index + 1:03d}",
                "displayName": f"Demo Driver {index + 1:03d}",
            },
        )
        drivers.append(int(driver["id"]))
        vehicle = post_json(
            args.api_url,
            "/api/v1/vehicles",
            {
                "externalReference": f"demo-vehicle-{stamp}-{index + 1:03d}",
                "make": "Demo Motors",
                "model": "Signal",
                "modelYear": 2024,
            },
        )
        vehicles.append(int(vehicle["id"]))

    base_time = datetime.now(UTC) - timedelta(minutes=args.events)
    odometers = [10_000.0 + index * 500 for index in range(args.drivers)]
    for index in range(args.events):
        driver_index = index % len(drivers)
        speed = max(0.0, min(160.0, randomizer.gauss(72, 20)))
        if randomizer.random() < 0.06:
            speed = randomizer.uniform(105, 135)
        odometers[driver_index] += max(speed / 60, 0.1)
        post_json(
            args.api_url,
            "/api/v1/events",
            {
                "driverId": drivers[driver_index],
                "vehicleId": vehicles[driver_index],
                "eventTimestamp": (base_time + timedelta(minutes=index)).isoformat(),
                "speedKmh": round(speed, 2),
                "hardBraking": randomizer.random() < 0.03,
                "rapidAcceleration": randomizer.random() < 0.04,
                "odometerKm": round(odometers[driver_index], 2),
                "latitude": round(randomizer.uniform(-0.25, 0.25), 6),
                "longitude": round(randomizer.uniform(-0.25, 0.25), 6),
            },
        )
    return args.events


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Load deterministic synthetic telemetry")
    parser.add_argument("--api-url", default="http://localhost:8080")
    parser.add_argument("--drivers", type=int, default=5)
    parser.add_argument("--events", type=int, default=500)
    parser.add_argument("--seed", type=int, default=42)
    args = parser.parse_args()
    if args.drivers < 1 or args.events < 1:
        parser.error("--drivers and --events must be positive")
    return args


def main() -> int:
    args = parse_args()
    try:
        count = generate(args)
        print(f"Created {count} synthetic telemetry events with seed {args.seed}.")
        return 0
    except HTTPError as error:
        print(f"API rejected seed data: HTTP {error.code} {error.read().decode()}", file=sys.stderr)
        return 1
    except URLError as error:
        print(f"Could not reach FleetSignal API: {error.reason}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
