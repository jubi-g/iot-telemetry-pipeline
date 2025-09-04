package com.itp.api_service.services.query.service;

import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;

import java.time.Duration;

public interface QueryService {
    Duration MAX_WINDOW = Duration.ofHours(24);
    SensorStatistics sensorStats(SensorStatsRequest request);
    GroupStatistics groupStats(GroupStatsRequest request);
}
