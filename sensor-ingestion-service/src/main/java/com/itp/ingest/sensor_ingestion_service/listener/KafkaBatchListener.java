package com.itp.ingest.sensor_ingestion_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.ingest.sensor_ingestion_service.config.IngestMetrics;
import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.service.BatchIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBatchListener implements BatchListener {
    private final ObjectMapper objectMapper;
    private final BatchIngestionService service;
    private final IngestMetrics metrics;

    @Override
    @KafkaListener(
        topics = "${app.topic:iot.readings.raw}",
        containerFactory = "kafkaListenerContainerFactory",
        concurrency = "1"
    )
    public void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        AtomicInteger parsed = new AtomicInteger();
        List<ReadingMessage> batch = new ArrayList<>(records.size());
        metrics.batchTimer().record(() -> {
            for (int i = 0; i < records.size(); i++) {
                var record = records.get(i);
                try {
                    batch.add(objectMapper.readValue(record.value(), ReadingMessage.class));
                    parsed.getAndIncrement();
                } catch (Exception e) {
                    metrics.invalid().increment();
                    log.warn("Invalid record at {}-{}@{}: {}", record.topic(), record.partition(), record.offset(), e.toString());
                    throw new BatchListenerFailedException("Parsing error", e, i);
                }
            }

            if (!batch.isEmpty()) {
                service.ingest(batch);

                ack.acknowledge();
                metrics.parsed().increment(parsed.doubleValue());
                metrics.total().increment(records.size());
            }
        });
    }
}
