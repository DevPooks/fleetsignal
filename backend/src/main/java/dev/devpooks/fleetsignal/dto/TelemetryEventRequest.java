package dev.devpooks.fleetsignal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.Instant;

public record TelemetryEventRequest(@NotNull Long driverId, @NotNull Long vehicleId,
		@NotNull @PastOrPresent Instant eventTimestamp,
		@NotNull @DecimalMin("0.0") @DecimalMax("250.0") BigDecimal speedKmh, boolean hardBraking,
		boolean rapidAcceleration, @NotNull @DecimalMin("0.0") BigDecimal odometerKm,
		@NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
		@NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude) {
}
