package dev.devpooks.fleetsignal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverCreateRequest(@NotBlank @Size(max = 64) String externalReference,
		@NotBlank @Size(max = 120) String displayName) {
}
