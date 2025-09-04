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

# Build and run services (simulator, ingest, aggregate, api)
./manage.sh run apps

# Take down infra and services
./manage.sh down all
```

## API cURLs (for local testing)

- ### Test Data
  * Sensor ID: `f7c69f0d-2c92-42c6-b508-80b56b33524d`
  * FROM Date: `2025-09-04T08:00:00Z`
  * TO Date: `2025-09-04T09:00:00Z`

- #### APIs (more available information in swagger)
  * #### Generate token
  ```
  curl --location 'localhost:8099/itp/api/v1/auth/token' \
  --header 'Content-Type: application/json' \
  --data '{
      "scope": "admin read:stats"
  }'
  ```

  * #### /v1/stats
    * ##### Get sensor statistics by sensor-id
    ```
    curl --location 'localhost:8099/itp/api/v1/stats/sensor/f7c69f0d-2c92-42c6-b508-80b56b33524d?from=2025-09-04T08%3A00%3A00Z&to=2025-09-04T09%3A00%3A00Z' \
    --header 'Authorization: {{bearer-jwt}}'
    ```
    * ##### Get group statistics (house, zone, type)
    ```
    curl --location 'localhost:8099/itp/api/v1/stats/sensor/group?from=2025-09-04T08%3A00%3A00Z&to=2025-09-04T09%3A00%3A00Z&houseId=houseId-30&zone=ZoneB&type=HEART_RATE' \
    --header 'Authorization: {{bearer-jwt}}'
    ```
