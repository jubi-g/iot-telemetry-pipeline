package com.itp.ingest.sensor_ingestion_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.service.IngestionService;
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
public class KafkaReadingConsumer implements Consumer {
    private final ObjectMapper om;
    private final IngestionService service;

    @Override
    @KafkaListener(topics = "${app.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) throws Exception {
        try {
            var batch = new ArrayList<ReadingMessage>(records.size());
            for (var record : records) {
                batch.add(om.readValue(record.value(), ReadingMessage.class));
            }
            service.ingest(batch);
        } catch (Exception e) {
            log.error("Batch processing failed; will be retried up to 2x then DLQ; size={}", records.size(), e);
            throw e;
        }
    }
}
