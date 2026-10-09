package dev.devpooks.fleetsignal.dto;

import java.math.BigDecimal;

public record FleetSummaryResponse(long driverCount, long vehicleCount, long eventCount, long driversWithAnalytics,
		BigDecimal averageRiskScore) {
}
