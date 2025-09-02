package com.itp.ingest.sensor_ingestion_service.service;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;

import java.util.List;

public interface BatchIngestionService {
    void ingest(List<ReadingMessage> messages);
}
