# IoT Telemetry Pipeline

A minimal end-to-end pipeline for ingesting raw IoT sensor readings, publishing them to Kafka, and aggregating them into useful metrics. Designed for quick local spins with Docker Compose and simple Spring Boot services.

## Overview

* ### Components
  * `sensor-simulator` > emits synthetic readings to Kafka
  * `sensor-ingestion-service` > consumes raw readings; validates/transforms; persists to Postgres
  * `aggregate-service` > scheduled jobs that computes raw readings into aggregated data

* ### Tech Stack
  * Java 17 (Spring Boot)
  * Kafka
  * Postgres
  * Docker

## Quick start
```
# Build and run infra (kafka, postgres)
./manage.sh run infra

# Build and run services
./manage.sh run apps

# Take down infra and services
./manage.sh down all
```