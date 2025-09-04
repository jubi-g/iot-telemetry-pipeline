package com.itp.api_service.services.query.model.dto;

import com.itp.api_service._commons.model.dto.CacheKeyAware;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TimeWindowRequest implements CacheKeyAware {
    private Instant from;
    private Instant to;

    @Override
    public String cacheKey() {
        var min = ChronoUnit.MINUTES;
        return from.truncatedTo(min) + "|" + to.truncatedTo(min);
    }
}
