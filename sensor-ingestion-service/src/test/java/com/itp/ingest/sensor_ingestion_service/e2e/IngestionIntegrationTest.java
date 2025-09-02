package com.itp.ingest.sensor_ingestion_service.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class IngestionIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer("7.5.1");

    @Container
    static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("itp")
            .withUsername("itp")
            .withPassword("itp");

    @Autowired JdbcTemplate jdbc;

    static final String TOPIC = "e2e.iotp.readings";

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.flyway.enabled", () -> "true");
        r.add("spring.flyway.create-schemas", () -> "true");

        r.add("app.topic", () -> TOPIC);

        r.add("app.kafka.concurrency", () -> "1");
        r.add("app.kafka.backoff.ms", () -> "100");
        r.add("app.kafka.max.retries", () -> "1");
    }

    @Test
    @DisplayName("Kafka → ingestion → Postgres: two records land in iot.readings")
    void endToEnd_ingestsTwoRecords() throws Exception {
        // produce two raw readings (json)
        try (KafkaProducer<String,String> producer = new KafkaProducer<>(producerProps())) {
            var json1 = json(UUID.randomUUID().toString(),"sensor-1","temp","house-A","zone-1", Instant.now(), 12.34);
            var json2 = json(UUID.randomUUID().toString(),"sensor-2","hum" ,"house-B","zone-2", Instant.now(), 45.67);

            producer.send(new ProducerRecord<>(TOPIC, json1)).get();
            producer.send(new ProducerRecord<>(TOPIC, json2)).get();
        }

        // await until both are persisted
        await().atMost(20, SECONDS).pollInterval(1, SECONDS).untilAsserted(() -> {
            Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM iot.readings", Integer.class);
            assertThat(cnt).isNotNull().isGreaterThanOrEqualTo(2);
        });
    }

    private static Properties producerProps() {
        Properties p = new Properties();
        p.put("bootstrap.servers", kafka.getBootstrapServers());
        p.put("key.serializer", StringSerializer.class.getName());
        p.put("value.serializer", StringSerializer.class.getName());
        p.put("acks", "all");
        return p;
    }

    private static String json(String id, String name, String type, String house, String zone, Instant ts, double value) throws Exception {
        var node = new com.fasterxml.jackson.databind.node.ObjectNode(new ObjectMapper().getNodeFactory());
        node.put("sensorId", id);
        node.put("sensorName", name);
        node.put("type", type);
        node.put("houseId", house);
        node.put("zone", zone);
        node.put("timestamp", ts.toString());
        node.put("value", value);
        return node.toString();
    }
}
