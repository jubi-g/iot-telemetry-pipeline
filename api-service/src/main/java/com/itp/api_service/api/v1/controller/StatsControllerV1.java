package com.itp.api_service.api.v1.controller;

import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.api.v1.model.dto.ApiResponseBody;
import com.itp.api_service.api.v1.model.dto.stats.GroupQueryParams;
import com.itp.api_service.api.v1.model.dto.stats.GroupStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.SensorStatsResult;
import com.itp.api_service.api.v1.model.dto.stats.StatsQueryParams;
import com.itp.api_service.api.v1.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Stats")
@RequestMapping("/v1/stats")
@RequiredArgsConstructor
public class StatsControllerV1 {

    private final StatsService service;

    @GetMapping("/sensor/{sensorId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin','SCOPE_read:stats')")
    @Operation(
        summary = "Sensor statistics over a time window",
        security = @SecurityRequirement(name = "bearer-jwt"),
        parameters = {
            @Parameter(name = "sensorId", description = "Sensor UUID", required = true),
            @Parameter(name = "from", description = "Start (ISO-8601, e.g. 2025-09-03T11:45:00Z)", required = true,
                example = "2025-09-03T11:45:00Z"),
            @Parameter(name = "to", description = "End (ISO-8601, e.g. 2025-09-03T12:00:00Z)", required = true,
                example = "2025-09-03T12:00:00Z")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(schema = @Schema(implementation = SensorStatsResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (missing scope)"),
            @ApiResponse(responseCode = "400", description = "Bad request")
        }
    )
    public ApiResponseBody<SensorStatsResult> getSensorStatsById(@PathVariable UUID sensorId,
                                                                 @Valid @ModelAttribute StatsQueryParams params) {
        SensorStatsRequest request = SensorStatsRequest.of(sensorId, params);
        return new ApiResponseBody<>(HttpStatus.OK, service.getSensorStats(request));
    }

    @GetMapping("/sensor/group")
    @PreAuthorize("hasAnyAuthority('SCOPE_admin','SCOPE_read:stats')")
    @Operation(
        summary = "Grouped statistics by house/zone/type",
        security = @SecurityRequirement(name = "bearer-jwt"),
        parameters = {
            @Parameter(name = "houseId", required = true),
            @Parameter(name = "zone", required = true),
            @Parameter(name = "type", required = true, description = "sensor type"), // TODO: add type validation
            @Parameter(name = "from", required = true, example = "2025-09-03T11:45:00Z"),
            @Parameter(name = "to", required = true, example = "2025-09-03T12:00:00Z")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(schema = @Schema(implementation = GroupStatsResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (missing scope)"),
            @ApiResponse(responseCode = "400", description = "Bad request")
        }
    )
    public ApiResponseBody<GroupStatsResult> getGroupStats(@Valid @ModelAttribute GroupQueryParams params) {
        GroupStatsRequest request = GroupStatsRequest.of(params);
        return new ApiResponseBody<>(HttpStatus.OK, service.getGroupStats(request));
    }

}
