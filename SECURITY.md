# Security policy

## Reporting a vulnerability

Please do not open a public issue for a suspected vulnerability. Use GitHub's private vulnerability reporting feature for this repository and include the affected version, a minimal reproduction, and the impact you observed. Reports about the demo score's suitability for real-world risk decisions are out of scope because the project explicitly prohibits that use.

## Supported version

Security fixes target the current `main` branch. There are no maintained release branches yet.

## Security boundaries

FleetSignal is a local portfolio project, not an internet-ready service. It implements request validation, foreign keys, parameterized data access, environment-based secrets, bounded request sizes, safe error responses, and request IDs. It does **not** implement authentication, authorization, tenant isolation, TLS termination, rate limiting, or audit retention.

Do not expose the Compose environment directly to an untrusted network. Replace all example credentials before using the code outside an isolated development machine.
