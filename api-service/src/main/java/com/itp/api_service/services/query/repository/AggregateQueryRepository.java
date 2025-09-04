package com.itp.api_service.services.query.repository;

import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.exception.DatabaseValidationException;
import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;

public interface AggregateQueryRepository {
    SensorStatistics sensorStats(SensorStatsRequest request) throws DatabaseUnavailableException, DatabaseValidationException;
    GroupStatistics groupStats(GroupStatsRequest request) throws DatabaseUnavailableException, DatabaseValidationException;
}
