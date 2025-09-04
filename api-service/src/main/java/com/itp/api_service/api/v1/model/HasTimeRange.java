package com.itp.api_service.api.v1.model;

import java.time.Instant;

public interface HasTimeRange {
    Instant from();
    Instant to();
}
