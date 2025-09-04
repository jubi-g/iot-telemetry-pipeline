package com.itp.api_service.services.query.repository;

import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service.services.query.model.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.itp.api_service._commons.helpers.TimeHelper.odt;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostgresJdbcQueryRepository implements AggregateQueryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Value("${itp.agg.schema:iot}")
    private String aggSchema;

    @Override
    public SensorStatistics sensorStats(SensorStatsRequest request)  {
        log.info("DB req for sensorStats with key={}", request.cacheKey());
        try {
            final String sql = """
                SELECT
                    type,
                    COALESCE(SUM(cnt), 0)                   AS total_cnt,
                    MIN(min_val)                            AS min_val,
                    MAX(max_val)                            AS max_val,
                    CASE WHEN COALESCE(SUM(cnt),0) > 0
                        THEN SUM(avg_val * cnt) / SUM(cnt)
                        ELSE NULL END                      AS avg_val,
                    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY median) AS approx_median
                FROM iot.agg_sensor_minute
                WHERE sensor_id = ?
                    AND bucket_minute >= date_trunc('minute', ?::timestamptz)
                    AND bucket_minute <  date_trunc('minute', (?::timestamptz + interval '1 minute'))
                GROUP BY type;
            """.formatted(aggSchema);

            String type;
            Statistics stats = jdbcTemplate.query(sql, (rs, rn) -> Statistics.of(rs),
                    request.getSensorId(),
                    odt(request.getWindow().getFrom()),
                    odt(request.getWindow().getTo()))
                .stream()
                .findFirst()
                .orElse(new Statistics(null, 0, null, null, null, null));
            return new SensorStatistics(request.getSensorId(), stats);
        } catch (Exception e) {
            rethrowException(e);
            throw e;
        }
    }

    @Override
    public GroupStatistics groupStats(GroupStatsRequest request) {
        log.info("DB req for groupStats with key={}", request.cacheKey());
        try {
            final String sql = """
                SELECT
                    COALESCE(SUM(cnt), 0) AS total_cnt,
                    MIN(min_val)          AS min_val,
                    MAX(max_val)          AS max_val,
                    CASE WHEN COALESCE(SUM(cnt),0) > 0
                        THEN SUM(avg_val * cnt) / SUM(cnt)
                        ELSE NULL END    AS avg_val,
                    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY median) AS approx_median
                FROM %s.agg_group_minute
                WHERE house_id = ? AND zone = ? AND type = ?
                    AND bucket_minute >= date_trunc('minute', ?::timestamptz)
                    AND bucket_minute <  date_trunc('minute', (?::timestamptz + interval '1 minute'))
            """.formatted(aggSchema);

            Statistics stats = jdbcTemplate.query(sql, (rs, rn) -> Statistics.of(rs),
                    request.getHouseId(),
                    request.getZone(),
                    request.getType(),
                    odt(request.getWindow().getFrom()),
                    odt(request.getWindow().getTo()))
                .stream()
                .findFirst()
                .orElse(new Statistics(null, 0, null, null, null, null));

            return new GroupStatistics(request.getHouseId(), request.getZone(), request.getType(), stats);
        } catch (Exception e) {
            rethrowException(e);
            throw e;
        }
    }

    private void rethrowException(Exception e) {
        if (e instanceof DataIntegrityViolationException
            || e instanceof OptimisticLockingFailureException) {
            throw new DatabaseUnavailableException(ErrorType.DB_VALIDATE_ERROR, e);
        }
        throw new DatabaseUnavailableException(ErrorType.DATABASE_UNAVAILABLE, e);
    }
}
