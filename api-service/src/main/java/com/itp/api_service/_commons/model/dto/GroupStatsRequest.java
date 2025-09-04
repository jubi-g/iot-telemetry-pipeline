package com.itp.api_service._commons.model.dto;

import com.itp.api_service.services.query.model.dto.TimeWindowRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GroupStatsRequest implements CacheKeyAware {
    private String houseId;
    private String zone;
    private String type;
    private TimeWindowRequest window;

    @Override
    public String cacheKey() {
        return "group|" + houseId + "|" + zone + "|" + type + "|" + window.cacheKey();
    }
}
