package dev.devpooks.fleetsignal.service;

import dev.devpooks.fleetsignal.dto.DriverAnalyticsResponse;
import dev.devpooks.fleetsignal.dto.FleetSummaryResponse;
import dev.devpooks.fleetsignal.entity.DriverAnalytics;
import dev.devpooks.fleetsignal.exception.ResourceNotFoundException;
import dev.devpooks.fleetsignal.repository.DriverAnalyticsRepository;
import dev.devpooks.fleetsignal.repository.DriverRepository;
import dev.devpooks.fleetsignal.repository.TelemetryEventRepository;
import dev.devpooks.fleetsignal.repository.VehicleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
	private final DriverAnalyticsRepository analyticsRepository;
	private final DriverRepository driverRepository;
	private final VehicleRepository vehicleRepository;
	private final TelemetryEventRepository eventRepository;

	public AnalyticsService(DriverAnalyticsRepository analyticsRepository, DriverRepository driverRepository,
			VehicleRepository vehicleRepository, TelemetryEventRepository eventRepository) {
		this.analyticsRepository = analyticsRepository;
		this.driverRepository = driverRepository;
		this.vehicleRepository = vehicleRepository;
		this.eventRepository = eventRepository;
	}

	@Transactional(readOnly = true)
	public DriverAnalyticsResponse forDriver(Long driverId) {
		if (!driverRepository.existsById(driverId)) {
			throw new ResourceNotFoundException("DRIVER_NOT_FOUND", "Driver not found.");
		}
		DriverAnalytics analytics = analyticsRepository.findById(driverId)
				.orElseThrow(() -> new ResourceNotFoundException("ANALYTICS_NOT_READY",
						"Analytics have not been calculated for this driver."));
		return toResponse(analytics);
	}

	@Transactional(readOnly = true)
	public FleetSummaryResponse fleetSummary() {
		List<DriverAnalytics> rows = analyticsRepository.findAll();
		BigDecimal averageRisk = rows.isEmpty()
				? BigDecimal.ZERO.setScale(2)
				: rows.stream().map(DriverAnalytics::getRiskScore).reduce(BigDecimal.ZERO, BigDecimal::add)
						.divide(BigDecimal.valueOf(rows.size()), 2, RoundingMode.HALF_UP);
		return new FleetSummaryResponse(driverRepository.count(), vehicleRepository.count(), eventRepository.count(),
				rows.size(), averageRisk);
	}

	private DriverAnalyticsResponse toResponse(DriverAnalytics analytics) {
		return new DriverAnalyticsResponse(analytics.getDriverId(), analytics.getAverageSpeedKmh(),
				analytics.getMaxSpeedKmh(), analytics.getSpeedingEventCount(), analytics.getHardBrakingCount(),
				analytics.getRapidAccelerationCount(), analytics.getEventCount(), analytics.getEstimatedDistanceKm(),
				analytics.getRiskScore(), analytics.getCalculatedAt());
	}
}
