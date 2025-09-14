CREATE SCHEMA IF NOT EXISTS iot;

-- assumption: 1 id = 1 sensor type; but in real-world, sensor can have several types
CREATE TABLE IF NOT EXISTS iot.sensors (
  id         UUID PRIMARY KEY,
  name       TEXT NOT NULL,
  type       TEXT NOT NULL,
  house_id   TEXT NOT NULL,
  zone       TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS iot.readings (
  sensor_id   UUID NOT NULL REFERENCES iot.sensors(id) DEFERRABLE INITIALLY DEFERRED,
  sensor_name TEXT NOT NULL,
  type        TEXT NOT NULL,
  house_id    TEXT NOT NULL,
  zone        TEXT NOT NULL,
  ts          TIMESTAMPTZ NOT NULL,
  value       DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (sensor_id, ts)
);

-- Sensors indexes
CREATE INDEX IF NOT EXISTS ix_sensors_house_type_zone  ON iot.sensors (house_id, type, zone);

-- Readings indexes
CREATE INDEX IF NOT EXISTS ix_readings_house_type_ts  ON iot.readings (house_id, zone, type, ts DESC);
CREATE INDEX IF NOT EXISTS ix_readings_sensor_ts_desc ON iot.readings (sensor_id, ts DESC);
CREATE INDEX IF NOT EXISTS brin_readings_ts           ON iot.readings USING BRIN (ts) WITH (pages_per_range = 128);