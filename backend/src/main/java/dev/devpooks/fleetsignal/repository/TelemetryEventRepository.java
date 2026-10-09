package dev.devpooks.fleetsignal.repository;

import dev.devpooks.fleetsignal.entity.TelemetryEvent;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, Long> {
	@Query("""
			SELECT e FROM TelemetryEvent e
			WHERE e.driver.id = :driverId
			  AND (:start IS NULL OR e.eventTimestamp >= :start)
			  AND (:end IS NULL OR e.eventTimestamp <= :end)
			""")
	Page<TelemetryEvent> findForDriver(@Param("driverId") Long driverId, @Param("start") Instant start,
			@Param("end") Instant end, Pageable pageable);

	@Query("""
			SELECT e FROM TelemetryEvent e
			WHERE e.vehicle.id = :vehicleId
			  AND (:start IS NULL OR e.eventTimestamp >= :start)
			  AND (:end IS NULL OR e.eventTimestamp <= :end)
			""")
	Page<TelemetryEvent> findForVehicle(@Param("vehicleId") Long vehicleId, @Param("start") Instant start,
			@Param("end") Instant end, Pageable pageable);
}
