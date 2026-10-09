package dev.devpooks.fleetsignal.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TelemetryEventResponse(Long id, Long driverId, Long vehicleId, Instant eventTimestamp,
		BigDecimal speedKmh, boolean hardBraking, boolean rapidAcceleration, BigDecimal odometerKm, BigDecimal latitude,
		BigDecimal longitude, Instant createdAt) {
}
