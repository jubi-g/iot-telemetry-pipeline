package com.itp.ingest.sensor_ingestion_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.service.BatchIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaReadingListener implements BatchListener {
    private final ObjectMapper objectMapper;
    private final BatchIngestionService service;

    @Override
    @KafkaListener(
        topics = "${app.topic:iot.readings.raw}",
        containerFactory = "kafkaListenerContainerFactory",
        concurrency = "3"
    )
    public void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) throws Exception {
        var batch = new ArrayList<ReadingMessage>(records.size());
        for (var record : records) {
            batch.add(objectMapper.readValue(record.value(), ReadingMessage.class));
        }
        service.ingest(batch);
        ack.acknowledge();
    }
}
