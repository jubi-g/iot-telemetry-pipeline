package com.itp.ingest.sensor_ingestion_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class IngestMetrics {
    private final Timer batchTimer;
    private final Counter parsed, invalid, total;

    public IngestMetrics(MeterRegistry registry) {
        this.batchTimer  = Timer.builder("ingest.batch.latency")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
        this.parsed = Counter.builder("ingest.records.parsed").register(registry);
        this.invalid= Counter.builder("ingest.records.invalid").register(registry);
        this.total  = Counter.builder("ingest.records.total").register(registry);
    }

    public Timer batchTimer() { return batchTimer; }
    public Counter parsed()   { return parsed; }
    public Counter invalid()  { return invalid; }
    public Counter total()    { return total; }

}
