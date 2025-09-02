CREATE SCHEMA IF NOT EXISTS iot;

-- Minute-level aggregates per sensor
CREATE TABLE IF NOT EXISTS iot.agg_sensor_minute (
  bucket_minute TIMESTAMPTZ NOT NULL,     -- minute bucket start, UTC
  sensor_id     UUID        NOT NULL,
  sensor_name   TEXT        NOT NULL,
  type          TEXT        NOT NULL,
  house_id      TEXT        NOT NULL,
  zone          TEXT        NOT NULL,
  cnt           BIGINT      NOT NULL,
  min_val       DOUBLE PRECISION NOT NULL,
  max_val       DOUBLE PRECISION NOT NULL,
  avg_val       DOUBLE PRECISION NOT NULL,
  median        DOUBLE PRECISION,
  PRIMARY KEY (bucket_minute, sensor_id)
);

CREATE INDEX IF NOT EXISTS ix_agg_sensor_minute_house_type_minute
  ON iot.agg_sensor_minute (house_id, type, bucket_minute DESC);

-- Minute-level aggregates per logical group (house+zone+type)
CREATE TABLE IF NOT EXISTS iot.agg_group_minute (
  bucket_minute TIMESTAMPTZ NOT NULL,
  house_id      TEXT        NOT NULL,
  zone          TEXT        NOT NULL,
  type          TEXT        NOT NULL,
  cnt           BIGINT      NOT NULL,
  min_val       DOUBLE PRECISION NOT NULL,
  max_val       DOUBLE PRECISION NOT NULL,
  avg_val       DOUBLE PRECISION NOT NULL,
  median        DOUBLE PRECISION,
  PRIMARY KEY (bucket_minute, house_id, zone, type)
);

CREATE INDEX IF NOT EXISTS ix_agg_group_minute_type_minute
  ON iot.agg_group_minute (type, bucket_minute DESC);
