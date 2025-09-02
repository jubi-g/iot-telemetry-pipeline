package com.itp.ingest.sensor_ingestion_service.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface BatchListener {
    void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) throws Exception;
}
