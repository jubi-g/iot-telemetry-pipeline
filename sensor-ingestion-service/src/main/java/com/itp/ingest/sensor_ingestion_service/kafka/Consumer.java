package com.itp.ingest.sensor_ingestion_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface Consumer {
    void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) throws Exception;
}
