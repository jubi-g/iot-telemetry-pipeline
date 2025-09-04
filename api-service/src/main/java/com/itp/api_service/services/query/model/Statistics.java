package com.itp.api_service.services.query.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.itp.api_service._commons.helpers.DsResultHelper.nullableDouble;
import static com.itp.api_service._commons.helpers.DsResultHelper.nullableString;

public record Statistics(
    String type,
    long totalCount,
    Double min,
    Double max,
    Double avg,
    Double median
) {
    public static Statistics of(ResultSet rs) throws SQLException {
        String type = nullableString(rs, "type");
        long total = rs.getLong("total_cnt");
        Double min = nullableDouble(rs, "min_val");
        Double max = nullableDouble(rs, "max_val");
        Double avg = nullableDouble(rs, "avg_val");
        Double med = nullableDouble(rs, "approx_median");
        return new Statistics(type, total, min, max, avg, med);
    }
}
