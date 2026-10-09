from __future__ import annotations

import argparse
import logging
import sys

from .calculations import calculate_driver_analytics
from .config import Settings
from .database import make_engine, read_events, write_analytics


logging.basicConfig(level=logging.INFO, format="%(levelname)s %(name)s %(message)s")
logger = logging.getLogger("fleetsignal.analytics")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Calculate FleetSignal driver analytics")
    parser.add_argument("--driver-id", type=int, help="Process one driver instead of the full fleet")
    return parser.parse_args()


def run(driver_id: int | None = None) -> int:
    settings = Settings.from_environment()
    engine = make_engine(settings.database_url)
    events = read_events(engine, driver_id)
    analytics = calculate_driver_analytics(events, settings.speeding_threshold_kmh)
    written = write_analytics(engine, analytics)
    logger.info("analytics_complete driver_filter=%s rows_written=%d", driver_id, written)
    return written


def main() -> int:
    args = parse_args()
    try:
        run(args.driver_id)
        return 0
    except (ValueError, OSError) as exception:
        logger.error("analytics_failed error=%s", exception)
        return 1
    except Exception:
        logger.exception("analytics_failed")
        return 1


if __name__ == "__main__":
    sys.exit(main())
