# Migrations

The executable Flyway migrations live in [`backend/src/main/resources/db/migration`](../../backend/src/main/resources/db/migration). Keeping the SQL on the backend runtime classpath lets the API apply and validate schema versions during startup without maintaining a duplicate copy here.
