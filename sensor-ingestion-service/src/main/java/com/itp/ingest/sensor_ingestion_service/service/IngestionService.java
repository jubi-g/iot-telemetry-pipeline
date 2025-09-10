package com.itp.ingest.sensor_ingestion_service.service;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.repository.JdbcBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngestionService implements BatchIngestionService {
    private final JdbcBatchRepository repository;

    @Override
    @Transactional
    public void ingest(List<ReadingMessage> messages) {
        if (messages.isEmpty()) return;
        repository.upsertSensors(messages); // upserts sensors; idempotent
        repository.insertReadings(messages); // inserts time-series values of readings
    }

}
