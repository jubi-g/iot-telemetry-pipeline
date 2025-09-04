package com.itp.api_service.api.v1.service;

import com.itp.api_service.api.v1.model.dto.stats.GroupStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.SensorStatsResult;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;

public interface StatsService {
    SensorStatsResult getSensorStats(SensorStatsRequest request);
    GroupStatsResult getGroupStats(GroupStatsRequest request);
}
