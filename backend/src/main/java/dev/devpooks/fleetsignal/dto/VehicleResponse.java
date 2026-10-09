package dev.devpooks.fleetsignal.dto;

import java.time.Instant;

public record VehicleResponse(Long id, String externalReference, String make, String model, Integer modelYear,
		Instant createdAt, Instant updatedAt) {
}
