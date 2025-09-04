package com.itp.api_service.api.v1.model.dto.stats;

import com.itp.api_service.api.v1.model.HasTimeRange;
import com.itp.api_service.api.v1.validation.ValidTimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@ValidTimeRange(maxHours = 24)
public record GroupQueryParams(
    @NotBlank(message = "houseId must have value")
    @NotNull(message = "houseId is required")
    String houseId,

    @NotBlank(message = "zone must have value")
    @NotNull(message = "zone is required")
    String zone,

    @NotBlank(message = "type must have value")
    @NotNull(message = "type is required")
    String type,

    @NotNull(message = "from is required")
    Instant from,

    @NotNull(message = "to is required")
    Instant to
) implements HasTimeRange {}