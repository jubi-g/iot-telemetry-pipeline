#!/usr/bin/env sh
set -e

DC="./manage.sh"
TOKEN_URL="http://localhost:8099/itp/api/v1/auth/token"
SENSOR_URL_BASE="http://localhost:8099/itp/api/v1/stats"

# 1) Start everything (infra + apps)
$DC run all --pull

# 2) Wait for API to be ready by polling the token endpoint
echo "⏳ Waiting for API (auth token endpoint) to be ready..."
TOKEN=""
for i in $(seq 1 30); do
  TOKEN=$(curl -s -X POST \
      -H "Content-Type: application/json" \
      -d '{"scope":"aggregate"}' \
      "$TOKEN_URL" \
    | jq -r '.data.token' 2>/dev/null || true)

  if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo "✅ JWT acquired."
    break
  fi

  echo "… still waiting ($i/30)"
  sleep 2
done

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ API did not become ready in time (no token). Check logs: ./manage.sh logs api"
  exit 1
fi

# 3) Fixed demo time window & sensor id
FROM="2025-09-04T08:00:00Z"
TO="2025-09-04T09:00:00Z"
SENSOR_ID="f7c69f0d-2c92-42c6-b508-80b56b33524d"

# 4a) Query SENSOR stats (GET, no body)
echo "➡️  Querying sensor stats for $SENSOR_ID [$FROM → $TO]"
curl -s \
  -H "Authorization: Bearer $TOKEN" \
  "$SENSOR_URL_BASE/sensor/${SENSOR_ID}?from=${FROM}&to=${TO}" | jq .

# 4b) Query GROUP stats (GET, no body)
echo "➡️  Querying group stats for houseId=houseId-30, zone=ZoneB, type=HEART_RATE [$FROM → $TO]"
curl -s \
  -H "Authorization: Bearer $TOKEN" \
  "$SENSOR_URL_BASE/sensor/group?houseId=houseId-30&zone=ZoneB&type=HEART_RATE&from=${FROM}&to=${TO}" | jq .

echo ""
echo "🎉 Quickstart complete!"
echo "Open Swagger UI: http://localhost:8099/itp/api/swagger-ui/index.html"
echo "Open Grafana:    http://localhost:3000 (admin/admin)"
echo "Open Kafka UI:   http://localhost:9000"
echo "Open Prometheus: http://localhost:9090/targets"
