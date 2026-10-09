package dev.devpooks.fleetsignal.repository;

import dev.devpooks.fleetsignal.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	boolean existsByExternalReference(String externalReference);
}
