ALTER TABLE iot.readings
  ADD CONSTRAINT readings_sensor_ts_uk UNIQUE (sensor_id, ts);
