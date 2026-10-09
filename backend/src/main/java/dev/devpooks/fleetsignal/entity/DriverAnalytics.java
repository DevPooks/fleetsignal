package dev.devpooks.fleetsignal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "driver_analytics")
public class DriverAnalytics {
	@Id
	@Column(name = "driver_id")
	private Long driverId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@Column(name = "average_speed_kmh", nullable = false)
	private BigDecimal averageSpeedKmh;

	@Column(name = "max_speed_kmh", nullable = false)
	private BigDecimal maxSpeedKmh;

	@Column(name = "speeding_event_count", nullable = false)
	private long speedingEventCount;

	@Column(name = "hard_braking_count", nullable = false)
	private long hardBrakingCount;

	@Column(name = "rapid_acceleration_count", nullable = false)
	private long rapidAccelerationCount;

	@Column(name = "event_count", nullable = false)
	private long eventCount;

	@Column(name = "estimated_distance_km", nullable = false)
	private BigDecimal estimatedDistanceKm;

	@Column(name = "risk_score", nullable = false)
	private BigDecimal riskScore;

	@Column(name = "calculated_at", nullable = false)
	private Instant calculatedAt;

	protected DriverAnalytics() {
	}

	public Long getDriverId() {
		return driverId;
	}
	public BigDecimal getAverageSpeedKmh() {
		return averageSpeedKmh;
	}
	public BigDecimal getMaxSpeedKmh() {
		return maxSpeedKmh;
	}
	public long getSpeedingEventCount() {
		return speedingEventCount;
	}
	public long getHardBrakingCount() {
		return hardBrakingCount;
	}
	public long getRapidAccelerationCount() {
		return rapidAccelerationCount;
	}
	public long getEventCount() {
		return eventCount;
	}
	public BigDecimal getEstimatedDistanceKm() {
		return estimatedDistanceKm;
	}
	public BigDecimal getRiskScore() {
		return riskScore;
	}
	public Instant getCalculatedAt() {
		return calculatedAt;
	}
}
