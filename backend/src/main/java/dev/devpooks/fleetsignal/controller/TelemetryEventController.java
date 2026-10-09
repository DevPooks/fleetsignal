package dev.devpooks.fleetsignal.controller;

import dev.devpooks.fleetsignal.dto.TelemetryEventRequest;
import dev.devpooks.fleetsignal.dto.TelemetryEventResponse;
import dev.devpooks.fleetsignal.service.TelemetryEventService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class TelemetryEventController {
	private final TelemetryEventService service;

	public TelemetryEventController(TelemetryEventService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<TelemetryEventResponse> create(@Valid @RequestBody TelemetryEventRequest request) {
		TelemetryEventResponse response = service.create(request);
		return ResponseEntity.created(URI.create("/api/v1/events/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public TelemetryEventResponse get(@PathVariable Long id) {
		return service.get(id);
	}
}
