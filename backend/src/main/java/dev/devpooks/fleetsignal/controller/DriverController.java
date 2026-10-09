package dev.devpooks.fleetsignal.controller;

import dev.devpooks.fleetsignal.dto.*;
import dev.devpooks.fleetsignal.service.AnalyticsService;
import dev.devpooks.fleetsignal.service.DriverService;
import dev.devpooks.fleetsignal.service.TelemetryEventService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {
	private final DriverService driverService;
	private final TelemetryEventService eventService;
	private final AnalyticsService analyticsService;

	public DriverController(DriverService driverService, TelemetryEventService eventService,
			AnalyticsService analyticsService) {
		this.driverService = driverService;
		this.eventService = eventService;
		this.analyticsService = analyticsService;
	}

	@PostMapping
	public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverCreateRequest request) {
		DriverResponse response = driverService.create(request);
		return ResponseEntity.created(URI.create("/api/v1/drivers/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public DriverResponse get(@PathVariable Long id) {
		return driverService.get(id);
	}

	@GetMapping("/{id}/events")
	public PageResponse<TelemetryEventResponse> events(@PathVariable Long id,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
		return eventService.forDriver(id, page, size, start, end);
	}

	@GetMapping("/{id}/analytics")
	public DriverAnalyticsResponse analytics(@PathVariable Long id) {
		return analyticsService.forDriver(id);
	}
}
