package com.itp.api_service.services.query.service;

import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.exception.DatabaseValidationException;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.api.v1.exception.QueryServiceException;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service.services.query.model.Statistics;
import com.itp.api_service.services.query.model.dto.TimeWindowRequest;
import com.itp.api_service.services.query.repository.AggregateQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueryServiceImplTest {

    AggregateQueryRepository repo;
    QueryServiceImpl service;

    Instant from = Instant.now().minusSeconds(300);
    Instant to = Instant.now();
    TimeWindowRequest tw = TimeWindowRequest.builder().from(from).to(to).build();

    @BeforeEach
    void setup() {
        repo = Mockito.mock(AggregateQueryRepository.class);
        service = new QueryServiceImpl(repo);
    }

    @Test
    void sensorStats_ok_returnsFromRepo() {
        SensorStatsRequest req = SensorStatsRequest.builder()
                .sensorId(UUID.randomUUID())
                .window(tw).build();
        SensorStatistics expected = new SensorStatistics(req.getSensorId(),
                new Statistics("temp", 10, 1.0, 9.0, 5.0, 5.5));
        when(repo.sensorStats(any())).thenReturn(expected);

        SensorStatistics got = service.sensorStats(req);

        assertEquals(expected, got);
        verify(repo).sensorStats(req);
    }

    @Test
    void groupStats_ok_returnsFromRepo() {
        GroupStatsRequest req = GroupStatsRequest.builder()
                .houseId("H1").zone("Z1").type("temp").window(tw).build();
        GroupStatistics expected = new GroupStatistics("H1","Z1","temp",
                new Statistics("temp", 10, 1.0, 9.0, 5.0, 5.5));
        when(repo.groupStats(any())).thenReturn(expected);

        GroupStatistics got = service.groupStats(req);

        assertEquals(expected, got);
        verify(repo).groupStats(req);
    }

    @Test
    void repoThrowsDatabaseValidation_passesThrough() {
        GroupStatsRequest req = GroupStatsRequest.builder()
                .houseId("H").zone("Z").type("t").window(tw).build();
        when(repo.groupStats(any())).thenThrow(new DatabaseValidationException(ErrorType.DB_VALIDATE_ERROR, "bad"));

        assertThrows(DatabaseValidationException.class, () -> service.groupStats(req));
    }

    @Test
    void repoThrowsDatabaseUnavailable_passesThrough() {
        SensorStatsRequest req = SensorStatsRequest.builder()
                .sensorId(UUID.randomUUID()).window(tw).build();
        when(repo.sensorStats(any())).thenThrow(new DatabaseUnavailableException(ErrorType.DATABASE_UNAVAILABLE, new RuntimeException("x")));

        assertThrows(DatabaseUnavailableException.class, () -> service.sensorStats(req));
    }

    @Test
    void repoThrowsGeneric_wrapsAsServiceError() {
        SensorStatsRequest req = SensorStatsRequest.builder()
                .sensorId(UUID.randomUUID()).window(tw).build();
        when(repo.sensorStats(any())).thenThrow(new RuntimeException("boom"));

        QueryServiceException ex = assertThrows(QueryServiceException.class, () -> service.sensorStats(req));
        assertEquals(ErrorType.SERVICE_ERROR, ex.getError());
    }
}
