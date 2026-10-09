package dev.devpooks.fleetsignal.service;

import dev.devpooks.fleetsignal.dto.VehicleCreateRequest;
import dev.devpooks.fleetsignal.dto.VehicleResponse;
import dev.devpooks.fleetsignal.entity.Vehicle;
import dev.devpooks.fleetsignal.exception.ConflictException;
import dev.devpooks.fleetsignal.exception.ResourceNotFoundException;
import dev.devpooks.fleetsignal.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {
	private final VehicleRepository repository;

	public VehicleService(VehicleRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public VehicleResponse create(VehicleCreateRequest request) {
		if (repository.existsByExternalReference(request.externalReference())) {
			throw new ConflictException("VEHICLE_REFERENCE_EXISTS",
					"A vehicle with this external reference already exists.");
		}
		Vehicle vehicle = new Vehicle(request.externalReference(), request.make(), request.model(),
				request.modelYear());
		return toResponse(repository.save(vehicle));
	}

	@Transactional(readOnly = true)
	public VehicleResponse get(Long id) {
		return toResponse(getEntity(id));
	}

	@Transactional(readOnly = true)
	public Vehicle getEntity(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("VEHICLE_NOT_FOUND", "Vehicle not found."));
	}

	private VehicleResponse toResponse(Vehicle vehicle) {
		return new VehicleResponse(vehicle.getId(), vehicle.getExternalReference(), vehicle.getMake(),
				vehicle.getModel(), vehicle.getModelYear(), vehicle.getCreatedAt(), vehicle.getUpdatedAt());
	}
}
