# Contributing

FleetSignal favors changes that make the existing Java → MySQL → Python path clearer or more reliable. Please open an issue before adding a new service or infrastructure dependency.

## Local checks

```bash
cp .env.example .env
make test
cd backend && ./mvnw spotless:check verify
docker compose config --quiet
```

Tests should explain the behavior they protect. Use synthetic identifiers and measurements only; do not commit real driver, vehicle, GPS, credential, or customer data.

## Pull requests

- Keep controllers thin and place transactional decisions in services.
- Add a Flyway migration for schema changes; never enable Hibernate schema creation in the runtime profile.
- Add failure-path coverage alongside success cases.
- Update the English and Spanish README when commands or user-visible behavior change.
- State which checks you actually ran.
