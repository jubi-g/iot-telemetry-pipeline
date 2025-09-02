package com.itp.ingest.sensor_ingestion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;

@EnableKafka
@EnableKafkaRetryTopic
@SpringBootApplication
public class SensorIngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorIngestionServiceApplication.class, args);
	}

}
