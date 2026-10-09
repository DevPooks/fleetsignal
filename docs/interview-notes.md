# Interview notes

## The 30-second explanation

FleetSignal accepts synthetic vehicle telemetry through a Spring Boot API, validates the measurements and driver/vehicle relationships, and stores the events in MySQL. A separate Python worker reads those rows, calculates deterministic driver metrics with Pandas, and upserts results that the API exposes. I kept it as two processes and one database so I could demonstrate API, SQL, and data-pipeline skills without hiding them behind distributed infrastructure.

## Spring Boot and dependency injection

Spring Boot configures the web server, JSON mapping, validation, JPA, and application lifecycle from a small set of dependencies. Dependencies are constructor-injected: for example, `TelemetryEventController` receives a `TelemetryEventService`, which receives repositories and entity services. That makes dependencies visible and lets unit tests substitute mocks without global state.

Controllers translate HTTP input/output. Services own decisions and transaction boundaries. Repositories express database access. Keeping those roles separate prevents controllers from becoming transaction scripts and keeps persistence details out of HTTP handling.

## JPA, MySQL, and Flyway

JPA maps Java entities to relational rows and lets Spring Data generate routine CRUD operations. Custom JPQL handles optional time filters. MySQL was selected because the problem is relational: events require existing drivers and vehicles, analytics belongs to exactly one driver, and indexes match known access paths.

Flyway applies versioned SQL. Hibernate uses `validate`, not schema creation, which catches mapping drift without mutating production state. Composite indexes start with `driver_id` or `vehicle_id` because those columns are equality filters, followed by `event_timestamp` for ordering/range access.

## Pagination

The endpoint accepts zero-based `page` and a `size` from 1 to 100. Spring Data sends `LIMIT`/`OFFSET` style queries plus a count query, then the API returns items and page metadata. Capping size prevents one request from loading an unbounded history. Cursor pagination would be preferable for very large or rapidly changing datasets.

## Testcontainers

Testcontainers starts a disposable MySQL 8 instance for a test class. Dynamic Spring properties point the application at that container, so the real Flyway SQL, connector, constraints, and JPA mappings run together. H2 tests stay useful for fast API feedback, but the MySQL test catches dialect-specific mistakes that H2 can miss.

## Python and Pandas

SQLAlchemy creates a pooled, parameterized database connection. Pandas reads only the columns needed for aggregation. The pipeline coerces numeric fields, drops malformed or impossible measurements, groups by `driver_id`, calculates metrics, and writes all result rows in one transaction using MySQL upsert semantics.

Estimated distance is max odometer minus min odometer. The score is `min(100, speeding × 2 + hard braking × 3 + rapid acceleration × 2)`. It is deterministic—same valid input, same score—and contains no machine learning.

The score is a portfolio demonstration only. It is not an insurance, safety, actuarial, or real-world decision model.

## Validation and errors

Bean Validation rejects malformed ranges at the HTTP boundary. Services verify referenced records and time-range ordering. MySQL repeats critical constraints. A controller advice converts expected failures to a stable `{error: ...}` document and logs unexpected exceptions without returning database internals. `X-Request-ID` is accepted or generated and returned for correlation.

## Docker Compose and CI

Compose supplies MySQL, the API, and an on-demand analytics profile. MySQL's health check gates API/worker startup instead of relying on a fixed sleep. GitHub Actions checks Java formatting, runs Maven verification and Python tests, compiles Python sources, validates Compose configuration, and builds both images.

## Tradeoffs I would discuss

- Batch analytics favors clarity and reproducibility; incremental aggregation would reduce staleness at higher complexity.
- Offset pagination is understandable but degrades at large offsets; keyset pagination is the production follow-up.
- The current analytics table stores one snapshot; history would require immutable snapshot rows and retention decisions.
- Authentication, authorization, tenant isolation, rate limiting, metrics, and backups are mandatory before network exposure.
- Batch ingestion and idempotency keys would be the first throughput/reliability improvements.

## Failure scenarios

- **Unknown driver/vehicle:** reject before insertion with `404` and a stable code.
- **Invalid measurement:** reject with `400`; the database constraint is a second guard.
- **Worker crash before commit:** no partial analytics transaction is committed; the next run can repeat safely.
- **Analytics never run:** the API returns `ANALYTICS_NOT_READY`, not misleading zeroes.
- **MySQL unavailable:** `/health` returns `503`, and startup/read/write operations fail visibly.
