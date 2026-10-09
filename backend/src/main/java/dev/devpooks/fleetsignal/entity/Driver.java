package dev.devpooks.fleetsignal.entity;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "drivers")
public class Driver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_reference", nullable = false, unique = true, length = 64)
	private String externalReference;

	@Column(name = "display_name", nullable = false, length = 120)
	private String displayName;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Driver() {
	}

	public Driver(String externalReference, String displayName) {
		this.externalReference = externalReference;
		this.displayName = displayName;
	}

	public Long getId() {
		return id;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public String getDisplayName() {
		return displayName;
	}
	public Instant getCreatedAt() {
		return createdAt;
	}
	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
