package dev.devpooks.fleetsignal.repository;

import dev.devpooks.fleetsignal.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
	boolean existsByExternalReference(String externalReference);
}
