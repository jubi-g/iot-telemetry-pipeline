package com.itp.api_service.api.v1.service;

import com.itp.api_service.api.v1.model.dto.stats.GroupStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.SensorStatsResult;
import com.itp.api_service.api.v1.service.impl.StatsServiceImpl;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service.services.query.model.Statistics;
import com.itp.api_service.services.query.model.dto.TimeWindowRequest;
import com.itp.api_service.services.query.service.QueryService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatsServiceImplTest {

    @Test
    void mapsSensorStatsToDto() {
        QueryService qs = mock(QueryService.class);
        StatsService svc = new StatsServiceImpl(qs);

        var tw = TimeWindowRequest.builder().from(Instant.now().minusSeconds(60)).to(Instant.now()).build();
        var req = SensorStatsRequest.builder().sensorId(UUID.randomUUID()).window(tw).build();
        var model = new SensorStatistics(req.getSensorId(), new Statistics("temp",1, 10.0, 10.0, 10.0, 10.0));
        when(qs.sensorStats(req)).thenReturn(model);

        SensorStatsResult dto = svc.getSensorStats(req);
        assertEquals(model.sensorId(), dto.sensorId());
        assertEquals(model.stats().min(), dto.statistics().min());
        verify(qs).sensorStats(req);
    }

    @Test
    void mapsGroupStatsToDto() {
        QueryService qs = mock(QueryService.class);
        StatsService svc = new StatsServiceImpl(qs);

        var tw = TimeWindowRequest.builder().from(Instant.now().minusSeconds(60)).to(Instant.now()).build();
        var req = GroupStatsRequest.builder().houseId("H1").zone("Z1").type("temp").window(tw).build();
        var model = new GroupStatistics("H1","Z1","temp", new Statistics("temp",2, 1.0, 2.0, 1.5, 1.5));
        when(qs.groupStats(req)).thenReturn(model);

        GroupStatsResult dto = svc.getGroupStats(req);
        assertEquals("H1", dto.houseNumber());
        assertEquals(2, dto.statistics().totalCount());
        verify(qs).groupStats(req);
    }

}
