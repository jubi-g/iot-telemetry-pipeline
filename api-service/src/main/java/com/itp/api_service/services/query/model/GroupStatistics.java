package com.itp.api_service.services.query.model;

public record GroupStatistics(
    String houseId,
    String zone,
    String type,
    Statistics stats
) {}

