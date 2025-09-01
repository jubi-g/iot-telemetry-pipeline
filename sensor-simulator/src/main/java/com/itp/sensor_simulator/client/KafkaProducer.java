package com.itp.sensor_simulator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.sensor_simulator.config.AppConfig;
import com.itp.sensor_simulator.model.dto.Reading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer implements ReadingProducer {
    private final AppConfig props;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void send(Reading r) {
        try {
            String key = r.sensorId();
            String json = objectMapper.writeValueAsString(r);
            kafkaTemplate.send(props.getTopic(), key, json);
            // log.info("Sent reading to {} with key {}: {}", props.getTopic(), key, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reading to Kafka", e);
        }
    }
}
