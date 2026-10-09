CREATE TABLE drivers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    external_reference VARCHAR(64) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_drivers_external_reference UNIQUE (external_reference)
);

CREATE TABLE vehicles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    external_reference VARCHAR(64) NOT NULL,
    make VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    model_year SMALLINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_vehicles_external_reference UNIQUE (external_reference),
    CONSTRAINT chk_vehicles_model_year CHECK (model_year BETWEEN 1980 AND 2100)
);

CREATE TABLE telemetry_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    event_timestamp TIMESTAMP(6) NOT NULL,
    speed_kmh DECIMAL(6,2) NOT NULL,
    hard_braking BOOLEAN NOT NULL DEFAULT FALSE,
    rapid_acceleration BOOLEAN NOT NULL DEFAULT FALSE,
    odometer_km DECIMAL(12,2) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_events_driver FOREIGN KEY (driver_id) REFERENCES drivers (id),
    CONSTRAINT fk_events_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT chk_events_speed CHECK (speed_kmh BETWEEN 0 AND 250),
    CONSTRAINT chk_events_odometer CHECK (odometer_km >= 0),
    CONSTRAINT chk_events_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT chk_events_longitude CHECK (longitude BETWEEN -180 AND 180),
    INDEX idx_events_driver_timestamp (driver_id, event_timestamp),
    INDEX idx_events_vehicle_timestamp (vehicle_id, event_timestamp),
    INDEX idx_events_timestamp (event_timestamp)
);

CREATE TABLE driver_analytics (
    driver_id BIGINT NOT NULL,
    average_speed_kmh DECIMAL(6,2) NOT NULL,
    max_speed_kmh DECIMAL(6,2) NOT NULL,
    speeding_event_count BIGINT NOT NULL,
    hard_braking_count BIGINT NOT NULL,
    rapid_acceleration_count BIGINT NOT NULL,
    event_count BIGINT NOT NULL,
    estimated_distance_km DECIMAL(12,2) NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL,
    calculated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (driver_id),
    CONSTRAINT fk_analytics_driver FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE CASCADE
);
