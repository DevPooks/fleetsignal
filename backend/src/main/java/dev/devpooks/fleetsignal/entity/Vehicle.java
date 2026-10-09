package dev.devpooks.fleetsignal.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "vehicles")
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_reference", nullable = false, unique = true, length = 64)
	private String externalReference;

	@Column(nullable = false, length = 80)
	private String make;

	@Column(nullable = false, length = 80)
	private String model;

	@Column(name = "model_year", nullable = false)
	private Integer modelYear;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Vehicle() {
	}

	public Vehicle(String externalReference, String make, String model, Integer modelYear) {
		this.externalReference = externalReference;
		this.make = make;
		this.model = model;
		this.modelYear = modelYear;
	}

	public Long getId() {
		return id;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public String getMake() {
		return make;
	}
	public String getModel() {
		return model;
	}
	public Integer getModelYear() {
		return modelYear;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
