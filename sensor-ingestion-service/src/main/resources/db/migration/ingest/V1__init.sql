CREATE SCHEMA IF NOT EXISTS iot;

CREATE TABLE IF NOT EXISTS iot.sensors (
  id         UUID PRIMARY KEY,
  name       TEXT NOT NULL,
  type       TEXT NOT NULL,
  house_id   TEXT NOT NULL,
  zone       TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS iot.readings (
  id          BIGSERIAL PRIMARY KEY,
  sensor_id   UUID NOT NULL,
  sensor_name TEXT NOT NULL,
  type        TEXT NOT NULL,
  house_id    TEXT NOT NULL,
  zone        TEXT NOT NULL,
  ts          TIMESTAMPTZ NOT NULL,
  value       DOUBLE PRECISION NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_readings_sensor_ts      ON iot.readings (sensor_id, ts DESC);
CREATE INDEX IF NOT EXISTS ix_readings_house_zone_ts  ON iot.readings (house_id, zone, ts DESC);
CREATE INDEX IF NOT EXISTS ix_readings_house_type_ts  ON iot.readings (house_id, type, ts DESC);
