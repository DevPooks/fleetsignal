# Database model

Flyway applies the canonical migration at `backend/src/main/resources/db/migration/V1__create_schema.sql`. Hibernate runs with `ddl-auto=validate`, so entity drift stops startup instead of silently changing a production schema.

```mermaid
erDiagram
    DRIVERS ||--o{ TELEMETRY_EVENTS : produces
    VEHICLES ||--o{ TELEMETRY_EVENTS : reports
    DRIVERS ||--o| DRIVER_ANALYTICS : has

    DRIVERS {
        bigint id PK
        varchar external_reference UK
        varchar display_name
        timestamp created_at
        timestamp updated_at
    }
    VEHICLES {
        bigint id PK
        varchar external_reference UK
        varchar make
        varchar model
        smallint model_year
        timestamp created_at
        timestamp updated_at
    }
    TELEMETRY_EVENTS {
        bigint id PK
        bigint driver_id FK
        bigint vehicle_id FK
        timestamp event_timestamp
        decimal speed_kmh
        boolean hard_braking
        boolean rapid_acceleration
        decimal odometer_km
        decimal latitude
        decimal longitude
        timestamp created_at
    }
    DRIVER_ANALYTICS {
        bigint driver_id PK,FK
        decimal average_speed_kmh
        decimal max_speed_kmh
        bigint speeding_event_count
        bigint hard_braking_count
        bigint rapid_acceleration_count
        bigint event_count
        decimal estimated_distance_km
        decimal risk_score
        timestamp calculated_at
    }
```

## Constraints that carry meaning

- Driver and vehicle external references are unique stable demo identifiers.
- An event cannot exist without both a driver and a vehicle.
- Speed, odometer, latitude, and longitude ranges are checked at both API and database boundaries.
- `driver_analytics.driver_id` is simultaneously a primary key and foreign key. This models one current snapshot per driver.
- Deleting a driver cascades only its analytics row. Event history remains protected by the foreign key.

## Indexes

`(driver_id, event_timestamp)` and `(vehicle_id, event_timestamp)` match the two paginated history endpoints. The leading relationship key filters the result set; timestamp ordering keeps the most recent records cheap to retrieve. A separate timestamp index leaves room for fleet-wide time-window queries.

## Precision

Coordinates use `DECIMAL(9,6)`, speed uses `DECIMAL(6,2)`, and odometer/distance use `DECIMAL(12,2)`. Java maps those values to `BigDecimal`. The analytics dataframe uses numeric values during calculation and rounds at the output boundary before MySQL persists them.
