package com.itp.ingest.sensor_ingestion_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic readingsRaw(@Value("${app.topic:iot.readings.raw}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic readingsRawDlt(@Value("${app.topic:iot.readings.raw}") String topic) {
        return TopicBuilder.name(topic + ".DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
