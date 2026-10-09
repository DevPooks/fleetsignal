package dev.devpooks.fleetsignal.service;

import dev.devpooks.fleetsignal.dto.PageResponse;
import dev.devpooks.fleetsignal.dto.TelemetryEventRequest;
import dev.devpooks.fleetsignal.dto.TelemetryEventResponse;
import dev.devpooks.fleetsignal.entity.Driver;
import dev.devpooks.fleetsignal.entity.TelemetryEvent;
import dev.devpooks.fleetsignal.entity.Vehicle;
import dev.devpooks.fleetsignal.exception.ResourceNotFoundException;
import dev.devpooks.fleetsignal.repository.TelemetryEventRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryEventService {
	private final TelemetryEventRepository repository;
	private final DriverService driverService;
	private final VehicleService vehicleService;

	public TelemetryEventService(TelemetryEventRepository repository, DriverService driverService,
			VehicleService vehicleService) {
		this.repository = repository;
		this.driverService = driverService;
		this.vehicleService = vehicleService;
	}

	@Transactional
	public TelemetryEventResponse create(TelemetryEventRequest request) {
		Driver driver = driverService.getEntity(request.driverId());
		Vehicle vehicle = vehicleService.getEntity(request.vehicleId());
		TelemetryEvent event = new TelemetryEvent(driver, vehicle, request.eventTimestamp(), request.speedKmh(),
				request.hardBraking(), request.rapidAcceleration(), request.odometerKm(), request.latitude(),
				request.longitude());
		return toResponse(repository.save(event));
	}

	@Transactional(readOnly = true)
	public TelemetryEventResponse get(Long id) {
		return repository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("EVENT_NOT_FOUND", "Telemetry event not found."));
	}

	@Transactional(readOnly = true)
	public PageResponse<TelemetryEventResponse> forDriver(Long driverId, int page, int size, Instant start,
			Instant end) {
		driverService.getEntity(driverId);
		validateRange(start, end);
		Page<TelemetryEventResponse> events = repository.findForDriver(driverId, start, end, pageRequest(page, size))
				.map(this::toResponse);
		return PageResponse.from(events);
	}

	@Transactional(readOnly = true)
	public PageResponse<TelemetryEventResponse> forVehicle(Long vehicleId, int page, int size, Instant start,
			Instant end) {
		vehicleService.getEntity(vehicleId);
		validateRange(start, end);
		Page<TelemetryEventResponse> events = repository.findForVehicle(vehicleId, start, end, pageRequest(page, size))
				.map(this::toResponse);
		return PageResponse.from(events);
	}

	private PageRequest pageRequest(int page, int size) {
		if (page < 0 || size < 1 || size > 100) {
			throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");
		}
		return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventTimestamp"));
	}

	private void validateRange(Instant start, Instant end) {
		if (start != null && end != null && start.isAfter(end)) {
			throw new IllegalArgumentException("Start timestamp must not be after end timestamp.");
		}
	}

	private TelemetryEventResponse toResponse(TelemetryEvent event) {
		return new TelemetryEventResponse(event.getId(), event.getDriver().getId(), event.getVehicle().getId(),
				event.getEventTimestamp(), event.getSpeedKmh(), event.isHardBraking(), event.isRapidAcceleration(),
				event.getOdometerKm(), event.getLatitude(), event.getLongitude(), event.getCreatedAt());
	}
}
