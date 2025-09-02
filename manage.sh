#!/bin/sh
set -e

usage() {
  echo "Usage: $0 {run|stop|down} [infra|apps|all]"
  exit 1
}

ACTION=$1
TARGET=${2:-all}

case "$ACTION" in
  run)
    case "$TARGET" in
      ingest)
        echo "🚀 Starting app services (ingest)..."
        docker compose rm -sf sensor-ingestion-service
        docker compose build --no-cache sensor-ingestion-service
        docker compose up -d sensor-ingestion-service
        ;;
      infra)
        echo "🚀 Starting infra (Kafka, Kafdrop, Postgres, Prometheus, Grafana)..."
        docker compose build --no-cache kafka kafdrop iot-postgres prometheus grafana
        docker compose up -d kafka kafdrop iot-postgres prometheus grafana
        ;;
      apps)
        echo "🚀 Starting app services (ingestion, simulator, aggregator)..."
        docker compose build --no-cache sensor-ingestion-service sensor-simulator aggregate-service
        docker compose up -d sensor-ingestion-service sensor-simulator aggregate-service
        ;;
      all)
        echo "🚀 Starting infra + apps..."
        docker compose up -d kafka kafdrop iot-postgres prometheus grafana
        echo "⏳ Waiting for infra to initialize..."
        sleep 15
        docker compose up -d sensor-ingestion-service sensor-simulator aggregate-service
        ;;
      *) usage ;;
    esac
    ;;
  stop)
    case "$TARGET" in
      infra)
        echo "🛑 Stopping infra..."
        docker compose stop kafka kafdrop iot-postgres prometheus grafana
        ;;
      apps)
        echo "🛑 Stopping apps..."
        docker compose stop sensor-ingestion-service sensor-simulator aggregate-service
        ;;
      all)
        echo "🛑 Stopping everything..."
        docker compose stop
        ;;
      *) usage ;;
    esac
    ;;
  down)
    case "$TARGET" in
      infra)
        echo "💣 Removing infra (Kafka, Kafdrop, Postgres, Prometheus, Grafana)..."
        docker compose rm -sf kafka kafdrop iot-postgres prometheus grafana
        ;;
      apps)
        echo "💣 Removing apps..."
        docker compose rm -sf sensor-ingestion-service sensor-simulator aggregate-service
        ;;
      all)
        echo "💣 Removing everything (containers + networks + volumes)..."
        docker compose down -v --remove-orphans
        ;;
      *) usage ;;
    esac
    ;;
  *) usage ;;
esac

echo "✅ Done. Use 'docker compose ps' to check status."