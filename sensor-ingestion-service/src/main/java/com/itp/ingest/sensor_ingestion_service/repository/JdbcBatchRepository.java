package com.itp.ingest.sensor_ingestion_service.repository;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;

import java.util.List;

public interface JdbcBatchRepository {
    void upsertBatch(List<ReadingMessage> batch);
    void insertBatch(List<ReadingMessage> batch);
}
