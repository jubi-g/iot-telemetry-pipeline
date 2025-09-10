package com.itp.ingest.sensor_ingestion_service.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Tag("integration")
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
        r.add("spring.flyway.schemas", () -> "iot_ingest");
        r.add("spring.flyway.default-schema", () -> "iot_ingest");
        r.add("spring.flyway.locations", () -> "classpath:db/migration/ingest");

        r.add("app.topic", () -> TOPIC);

        r.add("app.kafka.concurrency", () -> "1");
        r.add("app.kafka.backoff.ms", () -> "100");
        r.add("app.kafka.max.retries", () -> "1");
    }

    @Test
    @DisplayName("Kafka → ingestion → Postgres: two records land in iot.readings")
    void endToEnd_ingestsTwoRecords() throws Exception {
        jdbc.update("DELETE FROM iot.readings"); // remove all current records

        // produce two raw readings (json)
        try (KafkaProducer<String,String> producer = new KafkaProducer<>(producerProps())) {
            var json1 = json(UUID.randomUUID().toString(),"sensor-1","temp","house-A","zone-1", Instant.now(), 12.34);
            var json2 = json(UUID.randomUUID().toString(),"sensor-2","temp2" ,"house-B","zone-2", Instant.now(), 45.67);

            producer.send(new ProducerRecord<>(TOPIC, json1)).get();
            producer.send(new ProducerRecord<>(TOPIC, json2)).get();
        }

        // await until both are persisted
        await().atMost(20, SECONDS).pollInterval(1, SECONDS).untilAsserted(() -> {
            Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM iot.readings", Integer.class);
            assertThat(cnt).isNotNull().isEqualTo(2);
        });
    }

    @Test
    @DisplayName("Kafka → ingestion → Postgres: one record land in iot.readings → DLQ: one invalid record land in DLT")
    void poisonRecordGoesToDLQ() throws Exception {
        var good = json(UUID.randomUUID().toString(),"sensor-1","temp","house-A","zone-1", Instant.now(), 12.34);
        var poison = poisonJson("invalid");

        var c = new KafkaConsumer<String,String>(dlqConsumerProps());
        c.subscribe(List.of(TOPIC + ".DLT"));
        while (c.assignment().isEmpty()) {
            c.poll(Duration.ofMillis(100));
        }
        c.seekToEnd(c.assignment());

        try (var p = new KafkaProducer<String,String>(producerProps())) {
            p.send(new ProducerRecord<>(TOPIC, good)).get();
            p.send(new ProducerRecord<>(TOPIC, poison)).get();
        }

        // DB didn't store good record
        await().atMost(20, SECONDS).untilAsserted(() -> {
            Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM iot.readings", Integer.class);
            assertThat(cnt).isEqualTo(1);
        });

        // DLQ should contain the poison record
        var recs = pollUntilAtLeast(c, 1, Duration.ofSeconds(20));
        assertThat(recs.count()).isEqualTo(1);
    }

    private static Properties producerProps() {
        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }

    private Properties dlqConsumerProps() {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dlq-test-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    private static String json(String id, String name, String type, String house, String zone, Instant ts, double value) throws Exception {
        var node = new ObjectNode(new ObjectMapper().getNodeFactory());
        node.put("sensorId", id);
        node.put("sensorName", name);
        node.put("type", type);
        node.put("houseId", house);
        node.put("zone", zone);
        node.put("timestamp", ts.toString());
        node.put("value", value);
        return node.toString();
    }

    private static String poisonJson(String anyString) throws Exception {
        var node = new ObjectNode(new ObjectMapper().getNodeFactory());
        node.put("invalid", anyString);
        return node.toString();
    }

    private ConsumerRecords<String, String> pollUntilAtLeast(
        KafkaConsumer<String, String> consumer,
        int expected,
        Duration timeout) {

        long deadline = System.currentTimeMillis() + timeout.toMillis();
        var all = ConsumerRecords.<String, String>empty();

        while (System.currentTimeMillis() < deadline) {
            var polled = consumer.poll(Duration.ofMillis(500));
            if (!polled.isEmpty()) {
                all = polled;
                if (all.count() >= expected) {
                    return all;
                }
            }
        }
        throw new AssertionError("Expected at least " + expected + " records, got " + all.count());
    }

}
