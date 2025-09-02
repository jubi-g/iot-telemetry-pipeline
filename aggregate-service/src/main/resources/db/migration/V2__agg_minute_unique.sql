ALTER TABLE iot.agg_sensor_minute
  ADD CONSTRAINT agg_sensor_minute_uk UNIQUE (bucket_minute, sensor_id);

ALTER TABLE iot.agg_group_minute
  ADD CONSTRAINT agg_group_minute_uk
  UNIQUE (bucket_minute, house_id, zone, type);
