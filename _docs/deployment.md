# Deployment Protocol

This document describes how to locally deploy and operate the IoT Telemetry Pipeline prototype. It covers prerequisites, environment setup, helper scripts, and verification steps.

---

## 1. Prerequisites

- **Docker** (Desktop or Engine) and **Docker Compose v2** (`docker compose version`)
- **Java 21 + Maven** if you plan to build locally (optional when using Compose build)
- macOS on Apple Silicon (M1/M2): Docker Desktop ≥ 4.25 recommended

---

## 2. Environment configuration

For this prototype, configuration values (including database credentials, Kafka topics, and JWT signing secrets) are defined directly in the service `application.properties` files and in the `docker-compose.yml`.  
This keeps the setup simple for local testing.

In a real-world deployment:

- **Secrets** (e.g. `AUTH_SIGN_SECRET`, database passwords) should not be hardcoded.  
  They would be managed via:
    - environment variables (`.env` files, Kubernetes secrets)
    - secret managers (HashiCorp Vault, AWS Secrets Manager, etc.)
    - encryption-at-rest for configs

- **Configuration** should be externalized, allowing different values for dev, staging, and production without changing code.

## 3. Operate with `manage.sh`

The project ships a helper script that wraps common Docker Compose actions.

```
Usage: ./manage.sh <action> [target] [--no-cache] [--pull] [--no-build]

Actions:
  run        Start containers (optionally build)
  stop       Stop containers
  down       Remove containers (and optionally volumes with 'down all')
  restart    Restart target (stop + run)
  logs       Tail logs for target (Ctrl-C to exit)
  ps         Show 'docker compose ps'

Targets:
  infra      kafka kafdrop iot-postgres prometheus grafana
  apps       sensor-ingestion-service sensor-simulator aggregate-service api-service
  ingest     sensor-ingestion-service only
  simulator  sensor-simulator only
  aggregate  aggregate-service only
  api        api-service only
  all        infra + apps (default)

Examples:
  ./manage.sh run infra --pull
  ./manage.sh run apps --no-cache
  ./manage.sh restart api
  ./manage.sh logs simulator
  ./manage.sh down all
```

## 4. Common use cases / scenarios

* Bring up everything (infra + services)
    ```bash
    ./manage.sh run all --pull    # first time: pulls fresh base images
    ./manage.sh run all           # faster subsequent runs
    ```
  
* Only infrastructure (Kafka, Postgres, Prometheus, Grafana, Kafka UI)
    ```bash
    ./manage.sh run infra
    ```
  
* Only application services (simulator, ingest, aggregate, api)
    ```bash
    ./manage.sh run apps
    ```
  
* Start / restart a single service
    ```bash
    ./manage.sh run api            # only API
    ./manage.sh restart ingest     # stop + start ingestion
    ./manage.sh logs aggregate     # live logs
    ```
  
* Stop / remove
    ```bash
    ./manage.sh stop all           # stop everything (keeps containers)
    ./manage.sh down apps          # remove app containers (keeps volumes)
    ./manage.sh down all           # remove containers, networks, and volumes
    ```

## 5. Access & health checks

After `./manage.sh run all`, open:

* API (Swagger UI): http://localhost:8099/itp/api/swagger-ui/index.html
* Grafana: http://localhost:3000 (default: admin / admin)
* Prometheus: http://localhost:9090/targets
* Kafka UI (Kafdrop): http://localhost:9000

## 6. API cURLs (for local testing)

* Test Data → seeded data for prototype
  * Sensor ID: `f7c69f0d-2c92-42c6-b508-80b56b33524d`
  * FROM Date: `2025-09-04T08:00:00Z`
  * TO Date: `2025-09-04T09:00:00Z`


* **API contracts** (more information in OpenAPI docs)

  * #### Generate token
    ```bash
    curl --location 'localhost:8099/itp/api/v1/auth/token' \
    --header 'Content-Type: application/json' \
    --data '{
        "scope": "admin read:stats"
    }'
    ```
    
  * ##### Get sensor statistics by sensor-id
    ```bash
    curl --location 'localhost:8099/itp/api/v1/stats/sensor/f7c69f0d-2c92-42c6-b508-80b56b33524d?from=2025-09-04T08%3A00%3A00Z&to=2025-09-04T09%3A00%3A00Z' \
    --header 'Authorization: {{bearer-jwt}}'
    ```

  * ##### Get group statistics (house, zone, type)
    ```bash
    curl --location 'localhost:8099/itp/api/v1/stats/sensor/group?from=2025-09-04T08%3A00%3A00Z&to=2025-09-04T09%3A00%3A00Z&houseId=houseId-30&zone=ZoneB&type=HEART_RATE' \
    --header 'Authorization: {{bearer-jwt}}'
    ```
