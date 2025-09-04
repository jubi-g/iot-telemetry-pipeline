package com.itp.api_service.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.api_service.api.v1.model.dto.stats.GroupStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.SensorStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.StatsResult;
import com.itp.api_service.api.v1.service.StatsService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@WebMvcTest(controllers = StatsControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "server.servlet.context-path=",
    "spring.mvc.servlet.path="
})
class StatsControllerV1Test {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean StatsService statsService;

    @Test
    void getSensorStats_returnsOkAndBody() throws Exception {
        var sid = UUID.randomUUID();
        var now = Instant.now();

        var result = new SensorStatsResult(
            sid,
            "HEART_RATE",
            new StatsResult(1, 1.0, 1.0, 1.0, 1.0)
        );
        when(statsService.getSensorStats(any())).thenReturn(result);

        mvc.perform(get("/v1/stats/sensor/{id}", sid)
                .param("from", now.minusSeconds(60).toString())
                .param("to", now.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.sensorId", is(sid.toString())))
            .andExpect(jsonPath("$.data.sensorType", is("HEART_RATE")));
    }

    @Test
    void getGroupStats_returnsOkAndBody() throws Exception {
        var now = Instant.now();

        var result = new GroupStatsResult(
            "H1",
            "Z1",
            "HEART_RATE",
            new StatsResult(2, 1.0, 2.0, 1.5, 1.5)
        );
        when(statsService.getGroupStats(any())).thenReturn(result);

        mvc.perform(get("/v1/stats/sensor/group")
                .param("houseId", "H1")
                .param("zone", "Z1")
                .param("type", "HEART_RATE")
                .param("from", now.minusSeconds(60).toString())
                .param("to", now.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.houseNumber", is("H1")))
            .andExpect(jsonPath("$.data.zoneNumber", is("Z1")))
            .andExpect(jsonPath("$.data.sensorType", is("HEART_RATE")))
            .andExpect(jsonPath("$.data.statistics.totalCount", is(2)));
    }

}
