package dev.devpooks.fleetsignal.service;

import dev.devpooks.fleetsignal.dto.DriverCreateRequest;
import dev.devpooks.fleetsignal.dto.DriverResponse;
import dev.devpooks.fleetsignal.entity.Driver;
import dev.devpooks.fleetsignal.exception.ConflictException;
import dev.devpooks.fleetsignal.exception.ResourceNotFoundException;
import dev.devpooks.fleetsignal.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {
	private final DriverRepository repository;

	public DriverService(DriverRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public DriverResponse create(DriverCreateRequest request) {
		if (repository.existsByExternalReference(request.externalReference())) {
			throw new ConflictException("DRIVER_REFERENCE_EXISTS",
					"A driver with this external reference already exists.");
		}
		return toResponse(repository.save(new Driver(request.externalReference(), request.displayName())));
	}

	@Transactional(readOnly = true)
	public DriverResponse get(Long id) {
		return toResponse(getEntity(id));
	}

	@Transactional(readOnly = true)
	public Driver getEntity(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DRIVER_NOT_FOUND", "Driver not found."));
	}

	private DriverResponse toResponse(Driver driver) {
		return new DriverResponse(driver.getId(), driver.getExternalReference(), driver.getDisplayName(),
				driver.getCreatedAt(), driver.getUpdatedAt());
	}
}
