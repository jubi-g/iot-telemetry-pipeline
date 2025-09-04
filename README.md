# IoT Telemetry Pipeline

A minimal end-to-end pipeline for ingesting raw IoT sensor readings, publishing them to Kafka, and aggregating them into useful metrics. Designed for quick local spins with Docker Compose and simple Spring Boot services.

## Overview

![Overview](./_docs/overview.png)

* ### Components

  * #### Services
    * `sensor-simulator` > emits synthetic readings to Kafka
    * `sensor-ingestion-service` > consumes raw readings; validates/transforms; persists to Postgres
    * `aggregate-service` > scheduled jobs that computes raw readings into aggregated data
    * `api-service` > modular monolith (for prototype)
      * `api` > REST controllers & request/response handling
      * `auth` > provides public token API for authorization
      * `query` > data aggregation & queries

* ### Tech Stack
  * Java 21 (Spring Boot)
  * Kafka
  * Postgres
  * Caffeine Cache
  * Prometheus
  * Docker

* ### Observability
  * `Grafana` -> http://localhost:3000/dashboards (admin/admin)
  * `Prometheus` -> http://localhost:9090/targets
  * `Kafka` -> http://localhost:9000
  * `OpenAPI` -> http://localhost:8099/itp/api/swagger-ui/index.html

## Quick start
```
# Build and run infra (kafka, postgres, prometheus, grafana)
./manage.sh run infra

# Build and run services
./manage.sh run apps (simulator, ingest, aggregate, api)

# Take down infra and services
./manage.sh down all
```