package dev.devpooks.fleetsignal.dto;

import java.time.Instant;

public record DriverResponse(Long id, String externalReference, String displayName, Instant createdAt,
		Instant updatedAt) {
}
