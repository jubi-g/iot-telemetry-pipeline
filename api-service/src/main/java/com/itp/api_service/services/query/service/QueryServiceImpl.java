package com.itp.api_service.services.query.service;

import com.itp.api_service.api.v1.exception.QueryServiceException;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.exception.DatabaseValidationException;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.services.query.model.GroupStatistics;
import com.itp.api_service.services.query.model.SensorStatistics;
import com.itp.api_service.services.query.repository.AggregateQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.itp.api_service._commons.validation.TimeValidator.assertValid;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final AggregateQueryRepository repository;


    @Override
    @Cacheable(
        cacheNames = "sensorStats",
        key = "#request.cacheKey()",
        sync = true
    )
    public SensorStatistics sensorStats(SensorStatsRequest request) {
        try {
            assertValid(request.getWindow().getFrom(), request.getWindow().getTo(), MAX_WINDOW);
            return repository.sensorStats(request);
        } catch (DatabaseValidationException | DatabaseUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new QueryServiceException(ErrorType.SERVICE_ERROR, e.getMessage());
        }
    }

    @Override
    @Cacheable(
        cacheNames = "groupStats",
        key = "#request.cacheKey()",
        sync = true
    )
    public GroupStatistics groupStats(GroupStatsRequest request) {
        try {
            assertValid(request.getWindow().getFrom(), request.getWindow().getTo(), MAX_WINDOW);
            return repository.groupStats(request);
        } catch (DatabaseValidationException | DatabaseUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new QueryServiceException(ErrorType.SERVICE_ERROR, e.getMessage());
        }
    }

}
