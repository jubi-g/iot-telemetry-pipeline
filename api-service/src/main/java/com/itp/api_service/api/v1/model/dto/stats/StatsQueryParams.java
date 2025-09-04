package com.itp.api_service.api.v1.model.dto.stats;

import com.itp.api_service.api.v1.model.HasTimeRange;
import com.itp.api_service.api.v1.validation.ValidTimeRange;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@ValidTimeRange(maxHours = 24)
public record StatsQueryParams(
    @NotNull(message = "from is required")
    Instant from,

    @NotNull(message = "to is required")
    Instant to
) implements HasTimeRange {}