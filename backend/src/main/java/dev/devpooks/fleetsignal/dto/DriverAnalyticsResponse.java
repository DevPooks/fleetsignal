package dev.devpooks.fleetsignal.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DriverAnalyticsResponse(Long driverId, BigDecimal averageSpeedKmh, BigDecimal maxSpeedKmh,
		long speedingEventCount, long hardBrakingCount, long rapidAccelerationCount, long eventCount,
		BigDecimal estimatedDistanceKm, BigDecimal riskScore, Instant calculatedAt) {
}
