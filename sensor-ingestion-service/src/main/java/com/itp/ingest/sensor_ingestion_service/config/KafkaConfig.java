package com.itp.ingest.sensor_ingestion_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }

    @Bean
    public TaskScheduler taskScheduler(@Value("${app.scheduler.poolSize:2}") int poolSize) {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(poolSize);
        ts.setThreadNamePrefix("ingest-scheduler-");
        ts.initialize();
        return ts;
    }

    @Bean
    public DefaultErrorHandler errorHandler(
        KafkaTemplate<Object, Object> template,
        @Value("${app.topic:iot.readings.raw}") String topic,
        @Value("${app.kafka.backoff.ms:500}") long backoffMs,
        @Value("${app.kafka.max.retries:3}") long maxRetries
    ) {
        var recoverer = new DeadLetterPublishingRecoverer(
            template,
            (rec, ex) -> new TopicPartition(topic + ".DLT", rec.partition())
        );
        var eh = new DefaultErrorHandler(recoverer, new FixedBackOff(backoffMs, maxRetries));
        eh.addNotRetryableExceptions(DeserializationException.class, IllegalArgumentException.class);
        return eh;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory,
        DefaultErrorHandler errorHandler,
        @Value("${app.kafka.concurrency:3}") int concurrency
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, String>();
        f.setConsumerFactory(consumerFactory);
        f.setBatchListener(true);
        f.getContainerProperties().setObservationEnabled(true);
        f.getContainerProperties().setMicrometerEnabled(true);
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        f.setCommonErrorHandler(errorHandler);
        f.setConcurrency(concurrency);
        return f;
    }
}