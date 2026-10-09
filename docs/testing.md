# Testing strategy

The test suite is split by feedback speed and by the kind of mistake each layer can catch.

## Java

Small unit tests exercise pagination metadata, page-size boundaries, and invalid time ranges. A Spring Boot API integration test uses an in-memory MySQL-compatible H2 database to cover driver/vehicle creation, event persistence, retrieval, relationship checks, and the negative-speed regression through the real HTTP validation layer.

`MySqlMigrationIntegrationTest` uses Testcontainers with `mysql:8.4`. It starts the application against an ephemeral MySQL instance, lets Flyway apply the real migration, and verifies repository persistence. The class uses `disabledWithoutDocker=true`; a developer without Docker still gets the fast suite instead of an opaque environment failure.

```bash
cd backend
./mvnw spotless:check verify
```

## Python

Pytest feeds small dataframes directly into the calculation boundary. Cases cover:

- per-driver average and maximum speed;
- speeding, braking, and acceleration counts;
- estimated odometer distance;
- empty input and stable output columns;
- malformed/null values and impossible measurements;
- deterministic scoring and the 100-point cap;
- environment-derived database configuration.

External services are not needed for these calculation tests.

```bash
cd analytics
python -m pytest
```

## What is intentionally not tested here

The repository does not benchmark throughput, simulate GPS hardware, or validate real driving behavior. The score has no scientific correctness criterion because it is an explicitly documented demo formula. Docker Compose smoke testing belongs in local/release verification; CI builds the images and relies on the MySQL Testcontainer for migration coverage.
