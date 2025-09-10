package com.itp.ingest.sensor_ingestion_service.repository;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;

import java.util.List;

public interface JdbcBatchRepository {
    void upsertSensors(List<ReadingMessage> batch);
    void insertReadings(List<ReadingMessage> batch);
}
