package dev.devpooks.fleetsignal.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.devpooks.fleetsignal.entity.Driver;
import dev.devpooks.fleetsignal.repository.TelemetryEventRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TelemetryEventServiceTest {
	private final TelemetryEventRepository repository = mock(TelemetryEventRepository.class);
	private final DriverService driverService = mock(DriverService.class);
	private final VehicleService vehicleService = mock(VehicleService.class);
	private final TelemetryEventService service = new TelemetryEventService(repository, driverService, vehicleService);

	@Test
	void rejectsAnInvertedTimeRangeBeforeQuerying() {
		when(driverService.getEntity(7L)).thenReturn(new Driver("driver-7", "Driver Seven"));
		Instant start = Instant.parse("2026-01-02T00:00:00Z");
		Instant end = Instant.parse("2026-01-01T00:00:00Z");

		assertThatThrownBy(() -> service.forDriver(7L, 0, 20, start, end)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("must not be after");
	}

	@Test
	void capsRequestedPageSize() {
		when(driverService.getEntity(7L)).thenReturn(new Driver("driver-7", "Driver Seven"));

		assertThatThrownBy(() -> service.forDriver(7L, 0, 101, null, null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("between 1 and 100");
	}
}
