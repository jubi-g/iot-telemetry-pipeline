package com.itp.ingest.sensor_ingestion_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import com.itp.ingest.sensor_ingestion_service.service.BatchIngestionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class KafkaBatchListenerTest {
    @Test
    void onBatchMessage_parsesJson_and_callsService_and_acks() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        BatchIngestionService service = mock(BatchIngestionService.class);
        Acknowledgment ack = mock(Acknowledgment.class);
        KafkaBatchListener listener = new KafkaBatchListener(mapper, service);

        List<ConsumerRecord<String, String>> records = new ArrayList<>();
        String json1 = "{\"sensorId\":\"" + UUID.randomUUID() + "\",\"sensorName\":\"s1\",\"type\":\"temp\",\"houseId\":\"h1\",\"zone\":\"z1\",\"timestamp\":\"" + Instant.now() + "\",\"value\":21.5}";
        String json2 = "{\"sensorId\":\"" + UUID.randomUUID() + "\",\"sensorName\":\"s2\",\"type\":\"hum\",\"houseId\":\"h2\",\"zone\":\"z2\",\"timestamp\":\"" + Instant.now() + "\",\"value\":55.0}";

        records.add(new ConsumerRecord<>("iot.readings.raw", 0, 0L, "k1", json1));
        records.add(new ConsumerRecord<>("iot.readings.raw", 0, 1L, "k2", json2));

        listener.onBatchMessage(records, ack);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ReadingMessage>> captor = ArgumentCaptor.forClass(List.class);
        verify(service, times(1)).ingest(captor.capture());
        List<ReadingMessage> batch = captor.getValue();
        assertThat(batch).hasSize(2);
        assertThat(batch.get(0).sensorName()).isEqualTo("s1");
        assertThat(batch.get(1).type()).isEqualTo("hum");

        verify(ack, times(1)).acknowledge();
        verifyNoMoreInteractions(service, ack);
    }

    @Test
    void onBatchMessage_skipsInvalidRecords() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        BatchIngestionService service = mock(BatchIngestionService.class);
        Acknowledgment ack = mock(Acknowledgment.class);
        KafkaBatchListener listener = new KafkaBatchListener(mapper, service);

        List<ConsumerRecord<String, String>> records = new ArrayList<>();
        String json = "{\"sensorId\":\"" + UUID.randomUUID() + "\",\"sensorName\":\"s1\",\"type\":\"temp\",\"houseId\":\"h1\",\"zone\":\"z1\",\"timestamp\":\"" + Instant.now() + "\",\"value\":21.5}";
        records.add(new ConsumerRecord<>("iot.readings.raw", 0, 0L, "k1", json));
        records.add(new ConsumerRecord<>("iot.readings.raw", 0, 1L, "k2", null));

        listener.onBatchMessage(records, ack);
        verify(ack, times(1)).acknowledge();
    }
}