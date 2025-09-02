package com.itp.ingest.sensor_ingestion_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.service.BatchIngestionService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBatchListener implements BatchListener {
    private final ObjectMapper objectMapper;
    private final BatchIngestionService service;
    private final MeterRegistry metrics;

    @Override
    @KafkaListener(
        topics = "${app.topic:iot.readings.raw}",
        containerFactory = "kafkaListenerContainerFactory",
        concurrency = "3"
    )
    public void onBatchMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) throws Exception {
        long start = System.nanoTime();
        int parsed = 0, invalid = 0;

        var batch = new ArrayList<ReadingMessage>(records.size());
        for (var record : records) {
            try {
                batch.add(objectMapper.readValue(record.value(), ReadingMessage.class));
                parsed++;
            } catch (Exception e) {
                invalid++;
                log.warn("Invalid record at {}-{}@{}: {}", record.topic(), record.partition(), record.offset(), e.toString());
            }
        }
        service.ingest(batch);
        ack.acknowledge();

        metrics.counter("ingest.records.invalid").increment(invalid);
        metrics.counter("ingest.records.parsed").increment(parsed);
        metrics.counter("ingest.records.total").increment(records.size());
        Timer.builder("ingest.batch.latency")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(metrics)
            .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }
}
