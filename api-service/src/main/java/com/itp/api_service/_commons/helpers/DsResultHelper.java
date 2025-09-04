package com.itp.api_service._commons.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class DsResultHelper {
    private DsResultHelper() {}

    public static String nullableString(ResultSet rs, String column) {
        return getNullableValue(rs, column, String.class);
    }

    public static Double nullableDouble(ResultSet rs, String column) {
        return getNullableValue(rs, column, Double.class);
    }

    private static  <T> T getNullableValue(ResultSet rs, String column, Class<T> type) {
        try {
            if (rs.wasNull()) return null;
            return rs.getObject(column, type);
        } catch (SQLException e) {
            return null;
        }
    }
}
