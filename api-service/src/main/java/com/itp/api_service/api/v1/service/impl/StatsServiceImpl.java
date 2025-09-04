package com.itp.api_service.api.v1.service.impl;

import com.itp.api_service.api.v1.model.dto.stats.GroupStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.SensorStatsResult;
import com.itp.api_service.api.v1.service.StatsService;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.services.query.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final QueryService service;

    @Override
    public SensorStatsResult getSensorStats(SensorStatsRequest request) {
        return SensorStatsResult.of(service.sensorStats(request));
    }

    @Override
    public GroupStatsResult getGroupStats(GroupStatsRequest request) {
        return GroupStatsResult.of(service.groupStats(request));
    }

}
