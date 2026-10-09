# Regression example: negative speed accepted

## The bug

An early event-ingestion path checked that `speedKmh` was present but did not enforce a lower bound. A payload with `-1` could reach persistence and corrupt later aggregates.

## Root cause

The range existed as a domain assumption but was not expressed at either input boundary. Relying on the analytics worker to clean the record would have hidden an ingestion defect rather than preventing it.

## Failing test first

`TelemetryApiIntegrationTest.rejectsNegativeSpeedAndReturnsFieldDetails` submits a complete event with `speedKmh: -1` and expects:

- HTTP `400`;
- error code `INVALID_REQUEST`;
- a `speedKmh` entry in the validation details;
- no event insertion.

## Fix

The request DTO now declares `@DecimalMin("0.0")`, while the Flyway migration adds `CHECK (speed_kmh BETWEEN 0 AND 250)`. The API provides the fast, helpful failure and MySQL supplies a second invariant for non-HTTP writers.

## Result

The regression test travels through JSON parsing, Bean Validation, the centralized exception handler, and the response serializer. It protects behavior, not merely one helper method.
