package dev.devpooks.fleetsignal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "telemetry_events")
public class TelemetryEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "driver_id", nullable = false)
	private Driver driver;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vehicle_id", nullable = false)
	private Vehicle vehicle;

	@Column(name = "event_timestamp", nullable = false)
	private Instant eventTimestamp;

	@Column(name = "speed_kmh", nullable = false, precision = 6, scale = 2)
	private BigDecimal speedKmh;

	@Column(name = "hard_braking", nullable = false)
	private boolean hardBraking;

	@Column(name = "rapid_acceleration", nullable = false)
	private boolean rapidAcceleration;

	@Column(name = "odometer_km", nullable = false, precision = 12, scale = 2)
	private BigDecimal odometerKm;

	@Column(nullable = false, precision = 9, scale = 6)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 9, scale = 6)
	private BigDecimal longitude;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected TelemetryEvent() {
	}

	public TelemetryEvent(Driver driver, Vehicle vehicle, Instant eventTimestamp, BigDecimal speedKmh,
			boolean hardBraking, boolean rapidAcceleration, BigDecimal odometerKm, BigDecimal latitude,
			BigDecimal longitude) {
		this.driver = driver;
		this.vehicle = vehicle;
		this.eventTimestamp = eventTimestamp;
		this.speedKmh = speedKmh;
		this.hardBraking = hardBraking;
		this.rapidAcceleration = rapidAcceleration;
		this.odometerKm = odometerKm;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}
	public Driver getDriver() {
		return driver;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public Instant getEventTimestamp() {
		return eventTimestamp;
	}
	public BigDecimal getSpeedKmh() {
		return speedKmh;
	}
	public boolean isHardBraking() {
		return hardBraking;
	}
	public boolean isRapidAcceleration() {
		return rapidAcceleration;
	}
	public BigDecimal getOdometerKm() {
		return odometerKm;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
}
