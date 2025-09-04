package com.itp.api_service.api.v1.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_ID_HEADER = "x-request-id";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String requestId = Optional.ofNullable(req.getHeader(REQUEST_ID_HEADER)).orElse(UUID.randomUUID().toString());
        MDC.put(REQUEST_ID, requestId);
        res.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

}