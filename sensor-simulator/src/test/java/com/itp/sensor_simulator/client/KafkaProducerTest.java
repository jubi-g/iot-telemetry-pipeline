package com.itp.sensor_simulator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itp.sensor_simulator.config.AppConfig;
import com.itp.sensor_simulator.model.SensorType;
import com.itp.sensor_simulator.model.dto.Reading;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaProducerTest {

    @Test
    void sendsSerializedReadingToConfiguredTopic() throws Exception {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        AppConfig appConfig = new AppConfig();
        appConfig.setTopic("iot.readings.raw");

        var producer = new KafkaProducer(appConfig, kafkaTemplate, objectMapper);

        Reading reading = new Reading("id","nm","house","ZoneA", SensorType.THERMOSTAT, Instant.now(), 22.5);
        producer.send(reading);

        var payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("iot.readings.raw"), eq("id"), payloadCaptor.capture());

        var json = payloadCaptor.getValue();
        assertThat(json).contains("\"sensorId\":\"id\"");
    }

}