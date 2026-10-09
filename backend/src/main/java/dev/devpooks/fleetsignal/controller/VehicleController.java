package dev.devpooks.fleetsignal.controller;

import dev.devpooks.fleetsignal.dto.*;
import dev.devpooks.fleetsignal.service.TelemetryEventService;
import dev.devpooks.fleetsignal.service.VehicleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
	private final VehicleService vehicleService;
	private final TelemetryEventService eventService;

	public VehicleController(VehicleService vehicleService, TelemetryEventService eventService) {
		this.vehicleService = vehicleService;
		this.eventService = eventService;
	}

	@PostMapping
	public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleCreateRequest request) {
		VehicleResponse response = vehicleService.create(request);
		return ResponseEntity.created(URI.create("/api/v1/vehicles/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public VehicleResponse get(@PathVariable Long id) {
		return vehicleService.get(id);
	}

	@GetMapping("/{id}/events")
	public PageResponse<TelemetryEventResponse> events(@PathVariable Long id,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
		return eventService.forVehicle(id, page, size, start, end);
	}
}
