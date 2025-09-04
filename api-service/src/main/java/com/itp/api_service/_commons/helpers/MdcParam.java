package com.itp.api_service._commons.helpers;

import org.slf4j.MDC;

import java.util.UUID;

public final class MdcParam {
    private MdcParam() {}

    public static UUID requestId() {
        try {
            return UUID.fromString(MDC.get("requestId"));
        } catch (Exception e) {
            // should not stop operation
            return null;
        }
    }
}
