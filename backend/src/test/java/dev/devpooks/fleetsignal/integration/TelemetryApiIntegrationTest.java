package dev.devpooks.fleetsignal.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.devpooks.fleetsignal.FleetSignalApplication;
import dev.devpooks.fleetsignal.repository.DriverRepository;
import dev.devpooks.fleetsignal.repository.TelemetryEventRepository;
import dev.devpooks.fleetsignal.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = FleetSignalApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TelemetryApiIntegrationTest {
	@Autowired
	MockMvc mvc;
	@Autowired
	TelemetryEventRepository eventRepository;
	@Autowired
	DriverRepository driverRepository;
	@Autowired
	VehicleRepository vehicleRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@BeforeEach
	void cleanDatabase() {
		jdbcTemplate.update("DELETE FROM driver_analytics");
		eventRepository.deleteAll();
		driverRepository.deleteAll();
		vehicleRepository.deleteAll();
	}

	@Test
	void createsAndRetrievesACompleteTelemetryEvent() throws Exception {
		long driverId = createDriver("drv-integration");
		long vehicleId = createVehicle("veh-integration");

		String event = """
				{
				  "driverId": %d,
				  "vehicleId": %d,
				  "eventTimestamp": "2026-01-01T12:00:00Z",
				  "speedKmh": 84.50,
				  "hardBraking": false,
				  "rapidAcceleration": true,
				  "odometerKm": 18234.75,
				  "latitude": -33.448890,
				  "longitude": -70.669265
				}
				""".formatted(driverId, vehicleId);

		mvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(event))
				.andExpect(status().isCreated()).andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.speedKmh").value(84.5)).andExpect(jsonPath("$.driverId").value(driverId));

		mvc.perform(get("/api/v1/drivers/{id}/events", driverId).param("size", "10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.items", hasSize(1))).andExpect(jsonPath("$.total_items").doesNotExist())
				.andExpect(jsonPath("$.totalItems").value(1));
	}

	@Test
	void rejectsNegativeSpeedAndReturnsFieldDetails() throws Exception {
		long driverId = createDriver("drv-regression");
		long vehicleId = createVehicle("veh-regression");
		String event = """
				{
				  "driverId": %d,
				  "vehicleId": %d,
				  "eventTimestamp": "2026-01-01T12:00:00Z",
				  "speedKmh": -1,
				  "odometerKm": 100,
				  "latitude": 0,
				  "longitude": 0
				}
				""".formatted(driverId, vehicleId);

		mvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(event))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.error.details.speedKmh").exists());
	}

	@Test
	void rejectsAnUnknownDriverWithoutLeakingDatabaseDetails() throws Exception {
		long vehicleId = createVehicle("veh-missing-driver");
		String event = """
				{
				  "driverId": 999999,
				  "vehicleId": %d,
				  "eventTimestamp": "2026-01-01T12:00:00Z",
				  "speedKmh": 40,
				  "odometerKm": 100,
				  "latitude": 0,
				  "longitude": 0
				}
				""".formatted(vehicleId);

		mvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(event))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.error.code").value("DRIVER_NOT_FOUND"))
				.andExpect(jsonPath("$.error.message").value("Driver not found."));
	}

	@Test
	void retrievesWorkerProducedAnalyticsAndFleetSummary() throws Exception {
		long driverId = createDriver("drv-analytics");
		createVehicle("veh-analytics");
		jdbcTemplate.update("""
				INSERT INTO driver_analytics (
				    driver_id, average_speed_kmh, max_speed_kmh, speeding_event_count,
				    hard_braking_count, rapid_acceleration_count, event_count,
				    estimated_distance_km, risk_score, calculated_at
				) VALUES (?, 72.50, 121.00, 2, 1, 1, 20, 34.25, 9.00, CURRENT_TIMESTAMP)
				""", driverId);

		mvc.perform(get("/api/v1/drivers/{id}/analytics", driverId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.riskScore").value(9.0)).andExpect(jsonPath("$.eventCount").value(20));

		mvc.perform(get("/api/v1/fleet/summary")).andExpect(status().isOk())
				.andExpect(jsonPath("$.driverCount").value(1)).andExpect(jsonPath("$.vehicleCount").value(1))
				.andExpect(jsonPath("$.driversWithAnalytics").value(1));
	}

	private long createDriver(String reference) throws Exception {
		String body = """
				{"externalReference":"%s","displayName":"Synthetic Driver"}
				""".formatted(reference);
		String result = mvc.perform(post("/api/v1/drivers").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		return Long.parseLong(result.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
	}

	private long createVehicle(String reference) throws Exception {
		String body = """
				{"externalReference":"%s","make":"Demo Motors","model":"Signal","modelYear":2024}
				""".formatted(reference);
		String result = mvc.perform(post("/api/v1/vehicles").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		return Long.parseLong(result.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));
	}
}
