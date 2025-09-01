package com.itp.ingest.sensor_ingestion_service.service;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.repository.JdbcBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngestionService {
    private final JdbcBatchRepository repository;

    public void ingest(List<ReadingMessage> messages) {
        if (messages.isEmpty()) return;
        repository.upsertBatch(messages); // upserts sensors; idempotent
        repository.insertBatch(messages); // inserts time-series values of readings
    }
}
