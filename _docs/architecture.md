## Architectural Justification

This prototype is built around an **event-driven flow**, which is a common way to handle continuous streams of IoT data. The main idea is that sensor readings are first collected, then safely stored, and finally made available for querying and analysis.

- **Data transport (Kafka)**  
  Kafka acts like a central “mailbox” where data is published by the simulator and consumed by downstream services.  
  *Why:* it allows services to be loosely coupled, helps absorb traffic spikes, and ensures data can be replayed if needed.
  *Assumption:* simulator data has gone through MQTT/HTTP validation before data is sent to kafka.

- **Ingestion service**  
  Reads data from Kafka and writes it into a database (Postgres).  
  *Why:* Postgres is reliable, well-understood, and gives strong consistency.  
  *Trade-off:* for very high volumes, a time-series database would perform better, but Postgres is sufficient as prototype.

- **Aggregation service**  
  Periodically calculates statistics (average, min, max, percentiles) so queries don’t need to scan all raw data.  
  *Why:* pre-computed results make queries much faster.
  *Trade-off:* results are slightly delayed because they are computed in batches.

- **API service**  
  Exposes endpoints for clients to query sensor statistics. It is organized as a modular monolith with separate modules for authentication and querying.  
  *Why:* this keeps responsibilities clear without adding the overhead of full microservices setup.
  *Trade-off:* less flexible to scale modules independently.

- **Security**  
  Endpoints are protected with JWT tokens containing scopes (e.g., `read:stats`).  
  *Why:* this mimics real-world API security models while keeping setup simple.  
  *Trade-off:* in production, a proper identity provider would be required.

- **Observability**  
  Metrics (e.g., ingestion rate, aggregation latency) are collected with Prometheus and visualized in Grafana.  
  *Why:* monitoring makes it easier to understand pipeline health and detect issues early.  
  *Trade-off:* only basic dashboards are included, no automated alerting yet.

**Known Limitations**
- Single-node Postgres (no replication).
- No dead-letter queue for malformed events.
- Aggregation is batch-based, not real-time.
- Duplicate protection relies on database constraints only.

---

## Foresight on Scalability

Right now this project is a working prototype, but there’s a clear path for how it could handle larger volumes of data in a real setup:

- **Handling more devices**: Kafka topics can be split up so that many consumers can process different sensors in parallel.
- **Faster insights**: Instead of waiting for scheduled batch jobs, the system could calculate statistics as the data arrives (real-time streaming).
- **Managing storage costs**: Old raw readings don’t need to stay in the main database. They can be moved to cheaper storage (like cloud buckets), while only the important summaries remain in the main database.
- **Making queries faster**: For very large datasets, a database that’s built for time-series or analytics could replace or complement Postgres.
- **Safer error handling**: Messages that fail validation could be sent to a separate “holding area” instead of being lost, so they can be checked or retried later.
- **Scaling the services**: Each service (ingestion, API) can be run in multiple copies so the system isn’t tied to a single machine.
- **Speeding up responses**: Frequently requested statistics can be cached, so clients don’t always have to wait for a full database lookup.

These changes would let the same architecture grow from a small demo into something that can handle continuous streams of IoT data at scale.
