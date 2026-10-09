.PHONY: dev up down build test test-java test-python analytics seed format clean

dev: up

up:
	docker compose up --build -d

down:
	docker compose down

build:
	cd backend && ./mvnw clean package

test: test-java test-python

test-java:
	cd backend && ./mvnw test

test-python:
	cd analytics && python -m pytest

analytics:
	docker compose --profile tools run --rm analytics

seed:
	python scripts/generate_events.py

format:
	cd backend && ./mvnw spotless:apply

clean:
	cd backend && ./mvnw clean
	rm -rf analytics/.pytest_cache analytics/src/__pycache__ analytics/tests/__pycache__
