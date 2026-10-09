package dev.devpooks.fleetsignal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleCreateRequest(@NotBlank @Size(max = 64) String externalReference,
		@NotBlank @Size(max = 80) String make, @NotBlank @Size(max = 80) String model,
		@NotNull @Min(1980) @Max(2100) Integer modelYear) {
}
